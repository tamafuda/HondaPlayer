package jp.co.honda.music.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.MediaSessionManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

import jp.co.honda.music.common.HondaConstants;
import jp.co.honda.music.logger.Logger;
import jp.co.honda.music.model.Media;
import jp.co.honda.music.player.R;
import jp.co.honda.music.zdccore.HondaSharePreference;
import jp.co.honda.music.zdccore.PlaybackStatus;
import jp.co.honda.music.zdccore.PlayerState;

import static android.R.attr.action;

/**
 * @Author: Hoang Vu
 * @Date: 2017/02/23
 */

public class MediaPlayerService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener,
        AudioManager.OnAudioFocusChangeListener {

    // Logger
    protected final Logger log = new Logger(MediaPlayerService.class.getSimpleName(), true);

    public static final String ACTION_PLAY = "jp.co.honda.music.ACTION_PLAY";
    public static final String ACTION_PAUSE = "jp.co.honda.music.ACTION_PAUSE";
    public static final String ACTION_PREVIOUS = "jp.co.honda.music.ACTION_PREVIOUS";
    public static final String ACTION_NEXT = "jp.co.honda.music.ACTION_NEXT";
    public static final String ACTION_STOP = "jp.co.honda.music.ACTION_STOP";
    public static final String CURRENT_PLAYBACK_TRACK = "jp.co.honda.music.zdccore.MediaPlayerService.CURRENT_PLAYBACK_TRACK";
    public static final String CURRENT_PLAYBACK_POSITION = "jp.co.honda.music.zdccore.MediaPlayerService.CURRENT_PLAYBACK_POSI";
    public static final String STATE_PLAY = "PLAY";
    public static final String STATE_PAUSE = "PAUSE";
    public static final String STATE_NEXT = "NEXT";
    public static final String STATE_PREVIOUS = "PREVIOUS";
    public static final String STATE_NEW = "NEW";
    private String mPlayerState;

    //notification id
    private static final int NOTIFICATION_ID = 101;

    //media mediaPlayer
    private MediaPlayer mediaPlayer;

    //MediaSession
    private MediaSessionManager mediaSessionManager;
    private MediaSessionCompat mediaSession;
    private MediaControllerCompat.TransportControls transportControls;

    // Used to pause/resume MediaPlayer
    private int resumePosition = -1;

    // Audio Focus
    private AudioManager trackManager;

    //song list
    private ArrayList<Media> mMediaList;

    //current position
    private int trackIndex = -1;
    private Media activeMedia;


    //Binder
    private final IBinder musicBind = new MusicBinder();

    /**
     * Service Binder
     */
    public class MusicBinder extends Binder {
        public MediaPlayerService getService() {
            return MediaPlayerService.this;
        }
    }

    /**
     * request Media focus
     */
    private boolean requestTrackFocus() {
        trackManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = trackManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            //Focus gained
            return true;
        }
        //Could not gain focus
        return false;
    }

    /**
     * @return true if track is removed
     */
    private boolean removeTrackFocus() {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED ==
                trackManager.abandonAudioFocus(this);
    }


    /**
     * Init Media player
     */
    private void initMediaPlayer() {
        log.d("initMediaPlayer");
        if (mediaPlayer == null)
            // New MediaPlayer instance
            mediaPlayer = new MediaPlayer();

        //Set up MediaPlayer event listeners
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.setOnInfoListener(this);
        //Reset so that the MediaPlayer is not pointing to another data source
        mediaPlayer.reset();


        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            // Set the data source to the mediaFile location
            mediaPlayer.setDataSource(activeMedia.getData());
        } catch (IOException e) {
            e.printStackTrace();
            stopSelf();
        }
        mediaPlayer.prepareAsync();
    }

    /**
     * Play media
     */
    private void playMedia() {
        log.d("playMedia");
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    /**
     * Stop media
     */
    private void stopMedia() {
        log.d("stopMedia");
        if (mediaPlayer == null) {
            return;
        }
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    /**
     * Pause media
     */
    private void pauseMedia() {
        log.d("pauseMedia");
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            resumePosition = mediaPlayer.getCurrentPosition();
        }
    }

    /**
     * Resume media
     */
    private void resumeMedia() {
        log.d("resumeMedia");
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(resumePosition);
            mediaPlayer.start();
        }
    }

    /**
     * Skip to next
     */
    private void skipToNext() {
        log.d("skipToNext");
        // If playing track is latest
        // Next to first track
        if (trackIndex == mMediaList.size() - 1) {
            trackIndex = 0;
            activeMedia = mMediaList.get(trackIndex);
        } else {
            //get next in playlist
            activeMedia = mMediaList.get(++trackIndex);
        }

        //Update stored index
        new HondaSharePreference(getApplicationContext()).storeTrackIndex(trackIndex);
        // Stop media
        stopMedia();
        //reset mediaPlayer
        mediaPlayer.reset();
        initMediaPlayer();
    }

    /**
     * Skip to previous
     */
    private void skipToPrevious() {
        log.d("skipToPrevious");
        if (trackIndex == 0) {
            //if first in playlist
            //set index to the last of audioList
            trackIndex = mMediaList.size() - 1;
            activeMedia = mMediaList.get(trackIndex);
        } else {
            //get previous in playlist
            activeMedia = mMediaList.get(--trackIndex);
        }

        //Update stored index
        new HondaSharePreference(getApplicationContext()).storeTrackIndex(trackIndex);

        stopMedia();
        //reset mediaPlayer
        mediaPlayer.reset();
        initMediaPlayer();
    }


    /**
     * Moves a cursor of audio player
     *
     * @param progress position where to move
     */
    public void seekTo(int progress) {
        if (mediaPlayer != null)
            mediaPlayer.seekTo(progress);
    }

    public void seekBy(int rel) {
        if (mediaPlayer != null) {
            int pos = mediaPlayer.getCurrentPosition();
            pos += rel;
            if (pos < 0)
                pos = 0;
            else if (pos > mediaPlayer.getDuration())
                pos = mediaPlayer.getDuration();
            mediaPlayer.seekTo(pos);
        }
    }


    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public int getStoredCurrentPlayerPosition() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (pref.contains(MediaPlayerService.CURRENT_PLAYBACK_POSITION)) {

        }

        return 0;
    }

    public boolean isPlaying() {
        return (mediaPlayer != null) ? mediaPlayer.isPlaying() : false;
    }

    /**
     * ACTION_AUDIO_BECOMING_NOISY -- change in audio outputs
     */
    private BroadcastReceiver becomingNoisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //pause audio on ACTION_AUDIO_BECOMING_NOISY
            pauseMedia();
            buildNotification(PlaybackStatus.PAUSED);
        }
    };

    /**
     * Register Noisy Receiver
     */
    private void registerBecomingNoisyReceiver() {
        //register after getting audio focus
        IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(becomingNoisyReceiver, intentFilter);
    }

    /**
     * Notification actions -> playbackAction()
     * 0 -> Play
     * 1 -> Pause
     * 2 -> Next track
     * 3 -> Previous track
     *
     * @param playbackStatus
     */
    private void buildNotification(PlaybackStatus playbackStatus) {

        int notificationAction = android.R.drawable.ic_media_pause;//needs to be initialized
        PendingIntent play_pauseAction = null;

        //Build a new notification according to the current state of the MediaPlayer
        if (playbackStatus == PlaybackStatus.PLAYING) {
            notificationAction = android.R.drawable.ic_media_pause;
            //create the pause action
            play_pauseAction = playbackAction(1);
        } else if (playbackStatus == PlaybackStatus.PAUSED) {
            notificationAction = android.R.drawable.ic_media_play;
            //create the play action
            play_pauseAction = playbackAction(0);
        }

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),
                R.drawable.hikaru1); //replace with your own image

        // Create a new Notification
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                // Hide the timestamp
                .setShowWhen(false)
                // Set the Notification style
                .setStyle(new NotificationCompat.MediaStyle()
                        // Attach our MediaSession token
                        .setMediaSession(mediaSession.getSessionToken())
                        // Show our playback controls in the compat view
                        .setShowActionsInCompactView(0, 1, 2))
                // Set the Notification color
                .setColor(getResources().getColor(R.color.colorAccent))
                // Set the large and small icons
                .setLargeIcon(largeIcon)
                .setSmallIcon(android.R.drawable.stat_sys_headset)
                // Set Notification content information
                .setContentText(activeMedia.getArtist())
                .setContentTitle(activeMedia.getAlbum())
                .setContentInfo(activeMedia.getTitle())
                // Add playback actions
                .addAction(android.R.drawable.ic_media_previous, "previous", playbackAction(3))
                .addAction(notificationAction, "pause", play_pauseAction)
                .addAction(android.R.drawable.ic_media_next, "next", playbackAction(2));

        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    /**
     * MediaSession and Notification actions
     */
    private void initMediaSession() throws RemoteException {
        log.d("Init Media Session");
        if (mediaSessionManager != null) {
            //mediaSessionManager exists
            return;
        }

        mediaSessionManager = (MediaSessionManager) getSystemService(Context.MEDIA_SESSION_SERVICE);
        // Create a new MediaSession
        mediaSession = new MediaSessionCompat(getApplicationContext(), HondaConstants.PREFERENCE_MEDIA_PLAYER);
        //Get MediaSessions transport controls
        transportControls = mediaSession.getController().getTransportControls();
        //set MediaSession -> ready to receive media commands
        mediaSession.setActive(true);
        //indicate that the MediaSession handles transport control commands
        // through its MediaSessionCompat.Callback.
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        //Set mediaSession's MetaData
        updateMetaData();

        // Attach Callback to receive MediaSession updates
        mediaSession.setCallback(new MediaSessionCompat.Callback() {
            // Implement callbacks
            @Override
            public void onPlay() {
                log.d("MediaSession- Callback - onPlay");
                super.onPlay();

                resumeMedia();
                buildNotification(PlaybackStatus.PLAYING);
            }

            @Override
            public void onPause() {
                log.d("MediaSession- Callback - onPause");
                super.onPause();

                pauseMedia();
                buildNotification(PlaybackStatus.PAUSED);
            }

            @Override
            public void onSkipToNext() {
                log.d("MediaSession- Callback - onSkipToNext");
                super.onSkipToNext();

                skipToNext();
                updateMetaData();
                buildNotification(PlaybackStatus.PLAYING);
            }

            @Override
            public void onSkipToPrevious() {
                log.d("MediaSession- Callback - onSkipToPrevious");
                super.onSkipToPrevious();

                skipToPrevious();
                updateMetaData();
                buildNotification(PlaybackStatus.PLAYING);
            }

            @Override
            public void onStop() {
                log.d("MediaSession- Callback - onStop");
                super.onStop();
                removeNotification();
                //Stop the service
                stopSelf();
            }

            @Override
            public void onSeekTo(long position) {
                super.onSeekTo(position);
            }
        });
    }


    private void updateMetaData() {
        Bitmap albumArt = BitmapFactory.decodeResource(getResources(),
                R.drawable.image5); //replace with medias albumArt
        // Update the current metadata
        mediaSession.setMetadata(new MediaMetadataCompat.Builder()
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, albumArt)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, activeMedia.getArtist())
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, activeMedia.getAlbum())
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, activeMedia.getTitle())
                .build());
    }

    private void removeNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    private void handleIncomingActions(Intent playbackAction) {
        if (playbackAction == null || playbackAction.getAction() == null) return;

        String actionString = playbackAction.getAction();
        if (actionString.equalsIgnoreCase(ACTION_PLAY)) {
            log.d("HandleIncoming - ACTION_PLAY");
            transportControls.play();
        } else if (actionString.equalsIgnoreCase(ACTION_PAUSE)) {
            log.d("HandleIncoming - ACTION_PAUSE");
            transportControls.pause();
        } else if (actionString.equalsIgnoreCase(ACTION_NEXT)) {
            log.d("HandleIncoming - ACTION_NEXT");
            transportControls.skipToNext();
        } else if (actionString.equalsIgnoreCase(ACTION_PREVIOUS)) {
            log.d("HandleIncoming - ACTION_PREVIOUS");
            transportControls.skipToPrevious();
        } else if (actionString.equalsIgnoreCase(ACTION_STOP)) {
            log.d("HandleIncoming - ACTION_STOP");
            transportControls.stop();
        }
    }

    /**
     * @param actionNumber
     * @return
     */
    private PendingIntent playbackAction(int actionNumber) {
        Intent playbackAction = new Intent(this, MediaPlayerService.class);
        switch (actionNumber) {
            case 0:
                // Play
                playbackAction.setAction(ACTION_PLAY);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            case 1:
                // Pause
                playbackAction.setAction(ACTION_PAUSE);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            case 2:
                // Next track
                playbackAction.setAction(ACTION_NEXT);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            case 3:
                // Previous track
                playbackAction.setAction(ACTION_PREVIOUS);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            default:
                break;
        }
        return null;
    }


    /**
     * Play media from broadcast receiver
     */
    private BroadcastReceiver playBroadCastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            log.d("Action is: " + action);
            //Get the new media index form SharedPreferences
            trackIndex = new HondaSharePreference(getApplicationContext()).loadTrackIndex();
            log.d("Broadcast Receiver recevied trackID : " + trackIndex);
            if (trackIndex != -1 && trackIndex < mMediaList.size()) {
                activeMedia = mMediaList.get(trackIndex);
            }
            String action = intent.getAction();
            if (action.equals(HondaConstants.BROADCAST_PLAY_RESTORE_TRACK)) {
                // Play new music
                log.d("PLAY RESTORE _BROADCAST");
                mPlayerState = STATE_PLAY;
                /*stopMedia();
                mediaPlayer.reset();
                initMediaPlayer();*/
                resumeMedia();
                updateMetaData();
                buildNotification(PlaybackStatus.PLAYING);

            } else if (action.equals(HondaConstants.BROADCAST_PLAY_NEXT_TRACK)) {
                log.d("SKIP TO NEXT _BROADCAST");
                mPlayerState = STATE_NEXT;
                skipToNext();
                buildNotification(PlaybackStatus.PLAYING);
                updateMetaData();
                // Play next audio
            } else if (action.equals(HondaConstants.BROADCAST_PLAY_STOP_TRACK)) {
                // Stop music
                log.d("STOP _BROADCAST");
                mPlayerState = STATE_PAUSE;
                pauseMedia();
                buildNotification(PlaybackStatus.PAUSED);
                updateMetaData();

            } else if (action.equals(HondaConstants.BROADCAST_PLAY_PREVIOUS_TRACK)) {
                // Play previous
                log.d("PREVIOUS _BROADCAST");
                mPlayerState = STATE_PREVIOUS;
                skipToPrevious();
                buildNotification(PlaybackStatus.PLAYING);
                updateMetaData();
            } else if (action.equals(HondaConstants.BROADCAST_PLAY_NEW_TRACK)) {
                // Play new music
                log.d("PLAY NEW _BROADCAST");
                mPlayerState = STATE_PLAY;
                stopMedia();
                mediaPlayer.reset();
                initMediaPlayer();
                playMedia();
                updateMetaData();
                buildNotification(PlaybackStatus.PLAYING);
            }else if (action.equals(HondaConstants.BROADCAST_UNBIND_SERVICE)) {
                // Play new music
                log.d("UNBIND SERVICE");
                mPlayerState = STATE_PAUSE;
                onDestroy();
                //onUnbind(this);
//                stopMedia();
//                mediaPlayer.reset();
//                initMediaPlayer();
//                playMedia();
//                updateMetaData();
//                buildNotification(PlaybackStatus.PLAYING);

            }

        }
    };

    private void register_playAudio() {
        //Register playNewMedia receiver
        IntentFilter filter = new IntentFilter(HondaConstants.BROADCAST_PLAY_RESTORE_TRACK);
        registerReceiver(playBroadCastReceiver, filter);
    }

    private void register_nextAudio() {
        //Register playNewMedia receiver
        IntentFilter filter = new IntentFilter(HondaConstants.BROADCAST_PLAY_NEXT_TRACK);
        registerReceiver(playBroadCastReceiver, filter);
    }

    private void register_previousAudio() {
        //Register playNewMedia receiver
        IntentFilter filter = new IntentFilter(HondaConstants.BROADCAST_PLAY_PREVIOUS_TRACK);
        registerReceiver(playBroadCastReceiver, filter);
    }

    private void register_pauseAudio() {
        //Register playNewMedia receiver
        IntentFilter filter = new IntentFilter(HondaConstants.BROADCAST_PLAY_STOP_TRACK);
        registerReceiver(playBroadCastReceiver, filter);
    }

    private void register_newAudio() {
        //Register playNewMedia receiver
        IntentFilter filter = new IntentFilter(HondaConstants.BROADCAST_PLAY_NEW_TRACK);
        registerReceiver(playBroadCastReceiver, filter);
    }
    private void unbind_service() {
        //Register playNewMedia receiver
        IntentFilter filter = new IntentFilter(HondaConstants.BROADCAST_UNBIND_SERVICE);
        registerReceiver(playBroadCastReceiver, filter);
    }


    // ======================================================
    // ===== OVERRIDE METHOD =====
    // ======================================================

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {

            // Load data from SharedPreferences
            HondaSharePreference storage = new HondaSharePreference(getApplicationContext());
            mMediaList = storage.loadTrackList();
            trackIndex = storage.loadTrackIndex();
            log.d("Current track index is : " + String.valueOf(trackIndex));

            if (trackIndex != -1 && trackIndex < mMediaList.size()) {
                // Index is in a valid range
                activeMedia = mMediaList.get(trackIndex);
                mPlayerState = STATE_NEW;
            } else {
                stopSelf();
            }

        } catch (NullPointerException e) {
            stopSelf();
        }
        // Request track focus
        if (requestTrackFocus() == false) {
            stopSelf();
        }

        if (mediaSessionManager == null) {
            try {
                initMediaSession();
                initMediaPlayer();
            } catch (RemoteException e) {
                e.printStackTrace();
                stopSelf();
            }
            buildNotification(PlaybackStatus.PLAYING);
        }
        //Handle Intent action from MediaSession.TransportControls
        handleIncomingActions(intent);
        //return START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onCreate() {
        super.onCreate();

        registerBecomingNoisyReceiver();
        register_playAudio();
        register_nextAudio();
        register_pauseAudio();
        register_previousAudio();
        register_newAudio();
        unbind_service();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            stopMedia();
            mediaPlayer.release();
        }
        removeTrackFocus();
        removeNotification();

        // Unregister broadcaseReceiver
        unregisterReceiver(becomingNoisyReceiver);
        unregisterReceiver(playBroadCastReceiver);

        // Clear cache track list
        //new HondaSharePreference(getApplicationContext()).clearCachedTrackPlayList();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        log.d("MediaPlayerService onUnbind");
        mediaSession.release();
        removeNotification();
        return super.onUnbind(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // Fix problem that onServiceConnected method is not called
        // Instead of return null -> Return IBinder
        log.d("MediaPlayerService onBind");
        return musicBind;
        //return null;
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        //Invoked when the audio focus of the system is updated.
        // Temporary fix bug when notification come
        focusChange = HondaConstants.AUDIO_FOCUS_CHANGE;
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                log.d("AUDIOFOCUS_GAIN");
                // resume playback
                if (mediaPlayer == null) {
                    initMediaPlayer();
                } else if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                }
                mediaPlayer.setVolume(1.0f, 1.0f);
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                log.d("AUDIOFOCUS_LOSS");
                // Lost focus for an unbounded amount of time: stop playback and release media player
                if (mediaPlayer.isPlaying()) mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                log.d("AUDIOFOCUS_LOSS_TRANSIENT");
                // Lost focus for a short time, but we have to stop
                // playback. We don't release the media player because playback
                // is likely to resume
                if (mediaPlayer.isPlaying()) mediaPlayer.pause();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                log.d("AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
                // Lost focus for a short time, but it's ok to keep playing
                // at an attenuated level
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.setVolume(0.1f, 0.1f);
                }
                break;
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        log.d("onCompletion");
        log.d("State is : " + mPlayerState);
        if (mediaPlayer == null) {
            // MediaPlayer will signal normal completion even on an error,
            // so we will stop here to avoid spamming the user.
            log.w("not continuing playback due to error");
            return;
        }

        if (mMediaList.isEmpty()) {
            log.e("Tracklist is empty. No action will be done on completion");
            return;
        }
        if(mPlayerState == null) {
            stopMedia();
            stopSelf();
            return;
        }
        // After play done, play to next track
        if(mPlayerState.equals(PlayerState.NEXT.getState())) {
            skipToNext();
        }

        // Invoked when playback of a media has completed
        //stopMedia();

        //removeNotification();
        // Stop service
        //stopSelf();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        //Invoked when there has been an error during an asynchronous operation
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                Log.d("MediaPlayer Error", "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK " + extra);
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Log.d("MediaPlayer Error", "MEDIA ERROR SERVER DIED " + extra);
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Log.d("MediaPlayer Error", "MEDIA ERROR UNKNOWN " + extra);
                break;
        }
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        log.d("onPrepared");
        playMedia();
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

    }

    public int getResumePosition() {
        return resumePosition;
    }

    public void setResumePosition(int resumePosition) {
        this.resumePosition = resumePosition;
    }
}