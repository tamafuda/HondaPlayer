package jp.co.honda.music.player;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

import jp.co.honda.music.common.HondaConstants;
import jp.co.honda.music.helper.NowPlayingSeekHelper;
import jp.co.honda.music.helper.PlayerViewHelper;
import jp.co.honda.music.logger.Logger;
import jp.co.honda.music.model.Media;
import jp.co.honda.music.service.MediaPlayerService;
import jp.co.honda.music.util.PlayerUtils;
import jp.co.honda.music.util.TrackUtil;
import jp.co.honda.music.zdccore.HondaSharePreference;

public abstract class BasePlayerActivity extends AppCompatActivity{
    // Logger
    protected final Logger log = new Logger(MusicPlayActivity.class.getSimpleName(), true);
    private ImageButton btnPrevious;
    private ImageButton btnPlay;
    private ImageButton btnPause;
    private ImageButton btnNext;
    private SeekBar mSeekbar;
    private TextView mElapsedTime;
    private TextView mRemainingTime;

    private int mOverrideCurPos = -1;

    // Media list variables
    private ArrayList<Media> mediaList;

    // Service
    private MediaPlayerService mPlaybackService;
    private Intent playIntent;

    // Binding
    private boolean serviceBound = false;

    private HondaSharePreference storage;
    private NowPlayingSeekHelper mNowPlayingSeekHelper;

    private int currentPosition;
    private boolean hasSeekbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());
        btnPrevious = (ImageButton) findViewById(R.id.btn_previous);
        btnPlay = (ImageButton) findViewById(R.id.btn_play);
        btnPause = (ImageButton) findViewById(R.id.btn_pause);
        btnNext = (ImageButton) findViewById(R.id.btn_next);
        mSeekbar = (SeekBar) findViewById(R.id.seekBar);
        mElapsedTime = (TextView) findViewById(R.id.id_time_start);
        mRemainingTime = (TextView) findViewById(R.id.id_time_end);
        mSeekbar.setOnSeekBarChangeListener(onSeekBarChangeListener);
        log.d("SeekBar is going on !!!");
        hasSeekbar = true;
        mSeekbar.setEnabled(false);

        btnPrevious.setOnClickListener(mOnclick);
        btnPlay.setOnClickListener(mOnclick);
        btnPause.setOnClickListener(mOnclick);
        btnNext.setOnClickListener(mOnclick);
        // Get song list from device
        mediaList = TrackUtil.getTrackList(this);
        storage = new HondaSharePreference(this);
        //mediaList = storage.loadTrackList();
    }

    NowPlayingSeekHelper.SeekEventCallback mSeekEventCallback = new NowPlayingSeekHelper.SeekEventCallback() {

        @Override
        public void onActionUp() {
            updateProgressBars();
        }

        @Override
        public void updateActionHold(int seekJump) {
            NowPlayingSeekHelper.updateProgressBarsOnSeek(seekJump, mNowPlayingSeekHelper, mElapsedTime, mRemainingTime, mSeekbar);
        }

    };

    protected void initSeekbar(Media t) {
        int duration = Integer.parseInt(String.valueOf(t.getDuration()));
        if (duration <= 0 && mPlaybackService != null) {

            MediaPlayer mediaPlayer = mPlaybackService.getMediaPlayer();
            if (mediaPlayer != null) {
                duration = mediaPlayer.getDuration();
            }
        }
        if (mSeekbar != null) {
            mSeekbar.setMax(duration);
        }
    }


    /**
     * @param v
     */
    private View.OnClickListener mOnclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final int id = v.getId();
            switch (id) {
                case R.id.btn_previous:
                    previous();
                    break;
                case R.id.btn_play:
                    play();
                    //updateIconPlayPause();
                    break;
                case R.id.btn_pause:
                    pause();
                    break;
                case R.id.btn_next:
                    next();
                    break;
            }
        }
    };

    private final Handler mHandler = new Handler();
    private final Runnable mUpdatePositionRunnable = new Runnable() {
        @Override
        public void run() {
            updateProgressBars();
        }
    };

    //Binding this Client to the AudioPlayer Service
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            log.d("ServiceConnected Binder");

            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MediaPlayerService.MusicBinder binder = (MediaPlayerService.MusicBinder) service;
            mPlaybackService = binder.getService();
            serviceBound = true;
            storage.storeMPLServiceStatus(true);

            mNowPlayingSeekHelper = new NowPlayingSeekHelper(mUpdatePositionRunnable,
                    mHandler, btnNext, btnPrevious, mPlaybackService, mSeekEventCallback);
            mNowPlayingSeekHelper.setPlaybackService(mPlaybackService);
            updateIconPlayPause(mPlaybackService.isPlaying());
            initSeekbar(mediaList.get(storage.loadTrackIndex()));
            updateProgressBars();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            log.d("ServiceBound is false");
            serviceBound = false;
            storage.storeMPLServiceStatus(false);
        }
    };


    private void doUnbindService() {
        log.d("Do Unbind service !");
        if(mPlaybackService != null) {
            log.d("Release media player");
            mPlaybackService.stopSelf();
        }
        //stopService(new Intent(this,MediaPlayerService.class));
        /*Intent broadcastIntent = new Intent(HondaConstants.BROADCAST_UNBIND_SERVICE);
        sendBroadcast(broadcastIntent);*/
        unbindService(serviceConnection);
        serviceBound = false;
        storage.storeMPLServiceStatus(false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putBoolean("serviceStatus", serviceBound);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        serviceBound = savedInstanceState.getBoolean("serviceStatus");
    }


    @Override
    protected void onDestroy() {
        log.d("BasePlayerActivity onDestroy");
        super.onDestroy();
        if(isNeedKeepMediaSrv()){
            log.d("BasePlayerActivity still keep service");
            return;
        }

        if (storage.loadMPLServiceStatus() && serviceConnection != null) {
            log.d("onDestroy - Unbind service ");
            mHandler.removeCallbacks(mUpdatePositionRunnable);
            doUnbindService();
        }

    }

    private void updateIconPlayPause(boolean isPlaying) {
        PlayerViewHelper.setPlayPauseButtonVisibility(btnPlay, btnPause, isPlaying);
    }


    public void play() {
        //Check is service is active
        if (!storage.loadMPLServiceStatus()) {
            log.d("MediaPlayService serviceBound is not started !");
            //Store Serializable audioList to SharedPreferences
            //HondaSharePreference storage = new HondaSharePreference(getApplicationContext());
            currentPosition = storage.loadTrackIndex();
            if(currentPosition == -1) {
                currentPosition = 0;
                storage.storeTrackIndex(0);
            }
            storage.storeTrackList(mediaList);
            //storage.storeTrackIndex(getAudioIndex());
            Intent playerIntent = new Intent(this, MediaPlayerService.class);
            startService(playerIntent);
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
            setProgressSeekBar(0);
            //mSeekbar.setProgress(0);
        } else {
            //Store the new audioIndex to SharedPreferences
            //HondaSharePreference storage = new HondaSharePreference(getApplicationContext());
            //storage.storeTrackIndex(getAudioIndex());
            //Service is active
            //Send a broadcast to the service -> PLAY_NEW_AUDIO
            Intent broadcastIntent;
            if(mPlaybackService == null) {
                return;
            }
            log.d("isResume play or not: " + String.valueOf(mPlaybackService.getResumePosition()));
            if(mPlaybackService.getResumePosition() != -1) {
                broadcastIntent = new Intent(HondaConstants.BROADCAST_PLAY_RESTORE_TRACK);
            }else {
                broadcastIntent = new Intent(HondaConstants.BROADCAST_PLAY_NEW_TRACK);
                //mSeekbar.setProgress(0);
                setProgressSeekBar(0);
            }

            sendBroadcast(broadcastIntent);
        }
        updateIconPlayPause(true);


    }

    public void pause() {

        //storage.storeTrackIndex(getAudioIndex());
        //Service is active
        //Send a broadcast to the service -> PLAY_NEW_AUDIO
        updateIconPlayPause(false);
        Intent broadcastIntent = new Intent(HondaConstants.BROADCAST_PLAY_STOP_TRACK);
        sendBroadcast(broadcastIntent);
        updateProgressBars();
    }

    public void next() {
        updateIconPlayPause(true);
        //Service is active
        //Send a broadcast to the service -> PLAY_NEW_AUDIO
        Intent broadcastIntent = new Intent(HondaConstants.BROADCAST_PLAY_NEXT_TRACK);
        sendBroadcast(broadcastIntent);
        updateProgressBars();
    }

    public void previous() {

        //storage.storeTrackIndex(getAudioIndex());
        updateIconPlayPause(true);
        //Service is active
        //Send a broadcast to the service -> PLAY_NEW_AUDIO
        Intent broadcastIntent = new Intent(HondaConstants.BROADCAST_PLAY_PREVIOUS_TRACK);
        sendBroadcast(broadcastIntent);
        updateProgressBars();
    }

    private SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        Boolean moving = false;

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean isByUser) {
            if (moving) {
                mOverrideCurPos = progress;
                updateProgressBars();
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

            moving = true;
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

            moving = false;
        }
    };

    private void updateProgressBars() {
        log.d("update Progress bars");
        if(!hasSeekbar) {
            return;
        }
        //mHandler.removeCallbacks(mUpdatePositionRunnable);
        if (mPlaybackService != null) {
            mHandler.postDelayed(mUpdatePositionRunnable, 100);
            log.d("PostDelayed after 1s");
            MediaPlayer mp = mPlaybackService.getMediaPlayer();
            if (mp == null) {
                return;
            }
            //log.d("Current position is : " + String.valueOf(mp.getCurrentPosition()));

            int curPos = mOverrideCurPos; // Use the position of dragged seekbar - if dragging at this moment
            if (curPos < 0)
                curPos = mp.getCurrentPosition(); // Otherwise use the current playback position

            if (curPos == 0) {
                curPos = mPlaybackService.getStoredCurrentPlayerPosition();
            }

            if (mElapsedTime != null && mRemainingTime != null) {
                mElapsedTime.setText(PlayerUtils.getTimeHoursMinutesSecondsString(curPos));
                mRemainingTime.setText("-" + PlayerUtils.getTimeHoursMinutesSecondsString(mSeekbar.getMax() - curPos));
            }

            if (mSeekbar != null) {
                mSeekbar.setProgress(curPos);
            }
        } else {
            if (mSeekbar != null) {
                mSeekbar.setProgress(0);
            }

            if (mElapsedTime != null && mRemainingTime != null) {
                mElapsedTime.setText(PlayerUtils.getTimeHoursMinutesSecondsString(0));
                mRemainingTime.setText(PlayerUtils.getTimeHoursMinutesSecondsString(0));
            }
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
    }


    // === ABSTRACT METHOD ====
    protected abstract int getLayoutResourceId();

    protected abstract int getAudioIndex();



/*    @Override
    public void onBackPressed() {
        Intent iPlay = new Intent(getBaseContext(), RadarMusicActivity.class);
        //iPlay.putExtra(HondaConstants.DETECTED_SCREEN_FLING, true);
        startActivity(iPlay);
        finish();
    }*/
    protected abstract boolean isNeedKeepMediaSrv();

    public void playFromAdapter(){
        if (mPlaybackService != null) {
            mPlaybackService.setResumePosition(-1);
        }
        play();
    }
    public void stopFromChild() {
        if (mPlaybackService != null) {
            mPlaybackService.setResumePosition(-1);
        }
        pause();
    }

    private void setProgressSeekBar(int value) {
        if (hasSeekbar) {
            mSeekbar.setProgress(value);
            updateProgressBars();
        }
    }
}
