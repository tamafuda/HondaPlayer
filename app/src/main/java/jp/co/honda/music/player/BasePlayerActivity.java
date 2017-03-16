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
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import jp.co.honda.music.zdccore.AIMixInterface;
import jp.co.honda.music.zdccore.HondaSharePreference;

/**
 * @Author: Hoang Vu
 * @Date:   2017/02/23
 * This is base Activity for all Player activity related
 * We implemented all control about media such as PLAY, PAUSE, NEXT, PREVIOUS
 *
 */
public abstract class BasePlayerActivity extends AppCompatActivity {
    // Logger
    protected final Logger log = new Logger(BasePlayerActivity.class.getSimpleName(), true);
    private ImageButton btnPrevious;
    private ImageButton btnPlay;
    private ImageButton btnPause;
    private ImageButton btnNext;
    private ImageButton btnSave;
    private ImageButton btnShare;
    private SeekBar mSeekbar;
    private TextView mElapsedTime;
    private TextView mRemainingTime;

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
    private AIMixInterface mAIMixInterface;
    private boolean mIsStopMusic;
    private int mOverrideCurPos = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log.d("onCreate");
        storage = new HondaSharePreference(this);
        if(detectScreenID().equals(HondaConstants.DETECTED_SCREEN_CAPSUL)) {
            pause();
            return;
        }
        setContentView(getLayoutResourceId());
        if(getLayoutResourceId() == R.layout.activity_aimix_audio) {
            initScreen();
        }
        if (detectScreenID()!= null && detectScreenID().equals(HondaConstants.DETECT_FRAGMENT_FMAM)) {
            hasSeekbar = false;
        }
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

    /**
     * Init seekbar in case of pause, play a media file
     */
    protected void initSeekbar() {
        if(storage.loadTrackIndex() == -1) {
            storage.storeTrackIndex(0);
        }
        Media t = mediaList.get(storage.loadTrackIndex());
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
                    if(detectScreenID().equals(HondaConstants.DETECTED_SCREEN_ARRANGE)) {
                        mAIMixInterface.stopMedia();
                    }
                    break;
                case R.id.btn_pause:
                    pause();
                    break;
                case R.id.btn_next:
                    next();
                    break;
                case R.id.btn_save:
                    mAIMixInterface.saveMedia();
                    break;
                case R.id.btn_share:
                    mAIMixInterface.shareMedia();
                    break;
            }
        }
    };

    private final Handler mHandler = new Handler();
    private final Runnable mUpdatePositionRunnable = new Runnable() {
        @Override
        public void run() {
            log.d("run  mUpdatePositionRunnable");
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

            /*mNowPlayingSeekHelper = new NowPlayingSeekHelper(mUpdatePositionRunnable,
                    mHandler, btnNext, btnPrevious, mPlaybackService, mSeekEventCallback);
            mNowPlayingSeekHelper.setPlaybackService(mPlaybackService);*/
            updateIconPlayPause(hasSeekbar, mPlaybackService.isPlaying());
            initSeekbar();
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
        if (mPlaybackService != null) {
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
        if(mIsStopMusic) {
            storage.clearCachedTrackPlayList();
            if (storage.loadMPLServiceStatus() && serviceConnection != null) {
                log.d("onDestroy - Unbind service ");
                mHandler.removeCallbacks(mUpdatePositionRunnable);
                storage.storeMPLServiceStatus(false);
                //doUnbindService();
            }
        }
    }

    public void stopService() {
        if (isNeedKeepMediaSrv()) {
            log.d("BasePlayerActivity still keep service");
            return;
        }
        if (storage.loadMPLServiceStatus() && serviceConnection != null) {
            log.d("onDestroy - Unbind service ");
            mHandler.removeCallbacks(mUpdatePositionRunnable);
            doUnbindService();
        }
    }

    /**
     * isButtonController is true when seekbar is going on
     * @param isButtonController
     * @param isPlaying
     */
    private void updateIconPlayPause(boolean isButtonController, boolean isPlaying) {
        if (isButtonController) {
            PlayerViewHelper.setPlayPauseButtonVisibility(btnPlay, btnPause, isPlaying);
        }
    }

    /**
     * Play media method
     */
    public void play() {
        if(mediaList ==  null || mediaList.size() == 0){
            mediaList = storage.loadTrackList();
            if (mediaList == null || mediaList.size() == 0) {
                return;
            }
        }
        //Check is service is active
        if (!serviceBound) {
            log.d("MediaPlayService serviceBound is not started !");
            //Store Serializable audioList to SharedPreferences
            //HondaSharePreference storage = new HondaSharePreference(getApplicationContext());
            currentPosition = storage.loadTrackIndex();
            if (currentPosition == -1) {
                currentPosition = 0;
                storage.storeTrackIndex(0);
            }
            if(!detectScreenID().equals(HondaConstants.DETECT_FRAGMENT_FMAM)) {
                storage.storeTrackList(mediaList);
            }
            Intent playerIntent = new Intent(this, MediaPlayerService.class);
            startService(playerIntent);
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
            setProgressSeekBar(0);
        } else {
            Intent broadcastIntent;
            if (mPlaybackService == null) {
                return;
            }
            log.d("isResume play or not: " + String.valueOf(mPlaybackService.getResumePosition()));
            if (mPlaybackService.getResumePosition() != -1) {
                broadcastIntent = new Intent(HondaConstants.BROADCAST_PLAY_RESTORE_TRACK);
                updateProgressBars();
            } else {
                broadcastIntent = new Intent(HondaConstants.BROADCAST_PLAY_NEW_TRACK);
                //mSeekbar.setProgress(0);
                setProgressSeekBar(0);
            }

            sendBroadcast(broadcastIntent);
        }
        updateIconPlayPause(hasSeekbar, true);
        initSeekbar();
    }

    /**
     * Pause media method
     */
    public void pause() {
        updateIconPlayPause(hasSeekbar, false);
        Intent broadcastIntent = new Intent(HondaConstants.BROADCAST_PLAY_STOP_TRACK);
        sendBroadcast(broadcastIntent);
        updateProgressBars();

        mHandler.removeCallbacks(mUpdatePositionRunnable);
    }

    /**
     * Next to media
     */
    public void next() {
        updateIconPlayPause(hasSeekbar, true);
        Intent broadcastIntent = new Intent(HondaConstants.BROADCAST_PLAY_NEXT_TRACK);
        sendBroadcast(broadcastIntent);
        initSeekbar();
        updateProgressBars();
    }

    /**
     * Play a previous media
     */
    public void previous() {
        updateIconPlayPause(hasSeekbar, true);
        Intent broadcastIntent = new Intent(HondaConstants.BROADCAST_PLAY_PREVIOUS_TRACK);
        sendBroadcast(broadcastIntent);
        initSeekbar();
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

    /**
     * Update progressbar
     * It's still not working in some case
     * Ex : Do not update correctly pos or current pos
     * Should be fixed in next version (IF project is accepted by customer )
     */
    private void updateProgressBars() {
        if (!hasSeekbar) {
            stopUpdateSeekbar();
            return;
        }
        log.d("Seekbar is available!");
        if (mPlaybackService != null) {
            mHandler.postDelayed(mUpdatePositionRunnable, 1000);
            MediaPlayer mp = mPlaybackService.getMediaPlayer();
            if (mp == null) {
                return;
            }
            int curPos = mOverrideCurPos; // Use the position of dragged seekbar - if dragging at this moment
            if (curPos < 0){
                curPos = mp.getCurrentPosition(); // Otherwise use the current playback position
                log.d("Position is : " + String.valueOf(curPos));
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

    protected abstract boolean isNeedKeepMediaSrv();

    /**
     * Play media file from onClick row in listview
     */
    public void playFromAdapter() {
        if (mPlaybackService != null) {
            mPlaybackService.setResumePosition(-1);
        }
        play();
    }

    /**
     * Stop media from listview
     */
    public void stopFromChild() {
        if (mPlaybackService != null) {
            mPlaybackService.setResumePosition(-1);
        }
        pause();
    }

    /**
     * Set progressbar at current pos
     * @param value
     */
    private void setProgressSeekBar(int value) {
        log.d("setProgressSeekBar");
        if (hasSeekbar) {
            log.d("Seekbar is available!");
            mSeekbar.setProgress(value);
            updateProgressBars();
        }
    }

    protected abstract String detectScreenID();

    /**
     * This method will init view screen depend on screen typ
     * From AMFM/Bluetooth screen , does not show play media button group
     * Other case , show all
     */
    public void initScreen() {
        log.d("DetectScreenID is : " + detectScreenID());

        if (!detectScreenID().equals(HondaConstants.DETECT_FRAGMENT_FMAM)) {
            mediaList = storage.loadTrackList();
            log.d("It's not AMFM screen");
            btnPrevious = (ImageButton) findViewById(R.id.btn_previous);
            btnPlay = (ImageButton) findViewById(R.id.btn_play);
            btnPause = (ImageButton) findViewById(R.id.btn_pause);
            btnNext = (ImageButton) findViewById(R.id.btn_next);
            btnSave = (ImageButton) findViewById(R.id.btn_save);
            btnShare = (ImageButton) findViewById(R.id.btn_share);

            mSeekbar = (SeekBar) findViewById(R.id.seekBar);
            mElapsedTime = (TextView) findViewById(R.id.id_time_start);
            mRemainingTime = (TextView) findViewById(R.id.id_time_end);
            mSeekbar.setOnSeekBarChangeListener(onSeekBarChangeListener);
            log.d("SeekBar is going on !!!");
            hasSeekbar = true;
            updateProgressBars();
            log.d("Seekbar is enable");
            mSeekbar.setEnabled(false);
            btnPlay.setOnClickListener(mOnclick);
            btnPause.setOnClickListener(mOnclick);

            if(!detectScreenID().equals(HondaConstants.DETECTED_SCREEN_ARRANGE)) {
                LinearLayout ln = (LinearLayout) findViewById(R.id.play_layout);
                ln.setVisibility(View.VISIBLE);
                // Gets the layout params that will allow you to resize the layout
                LinearLayout frame = (LinearLayout) findViewById(R.id.frame_layout);
                ViewGroup.LayoutParams params = frame.getLayoutParams();
                // Changes the height and width to the specified *pixels*
                params.height = 400;
                frame.setLayoutParams(params);
                btnPrevious.setOnClickListener(mOnclick);
                btnNext.setOnClickListener(mOnclick);
            }else{
                setMediaListToBaseActivity();
                RelativeLayout.LayoutParams lp;
                lp = (RelativeLayout.LayoutParams) btnSave.getLayoutParams();
                lp.height = (int) this.getResources().getDimension(R.dimen.dimen_save_height);
                lp.width = (int) this.getResources().getDimension(R.dimen.dimen_save_width);
                btnSave.setVisibility(View.VISIBLE);
                btnSave.setLayoutParams(lp);
                btnShare.setVisibility(View.VISIBLE);
                lp = (RelativeLayout.LayoutParams) btnShare.getLayoutParams();
                lp.height = (int) this.getResources().getDimension(R.dimen.dimen_save_height);
                lp.width = (int) this.getResources().getDimension(R.dimen.dimen_save_width);
                btnShare.setLayoutParams(lp);
                btnPrevious.setVisibility(View.GONE);
                btnNext.setVisibility(View.GONE);
                btnSave.setOnClickListener(mOnclick);
                btnShare.setOnClickListener(mOnclick);
            }

        } else {
            log.d("It's AMFM screen");
            hasSeekbar = false;
            stopUpdateSeekbar();
            mediaList = TrackUtil.getRadioStationList(getBaseContext());
            LinearLayout ln = (LinearLayout) findViewById(R.id.play_layout);
            ln.setVisibility(View.GONE);
            // Gets the layout params that will allow you to resize the layout
            LinearLayout frame = (LinearLayout) findViewById(R.id.frame_layout);
            ViewGroup.LayoutParams params = frame.getLayoutParams();
            // Changes the height and width to the specified *pixels*
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            frame.setLayoutParams(params);
        }
    }

    /**
     * Interface to get action save/share button and send to AIMix
     * Stupid implementation because of change spec VERY VERY late
     * Anyway, stupid changed spec !!!
     * @param aiInterface
     */
    public void setAIInterface(AIMixInterface aiInterface) {
        this.mAIMixInterface = aiInterface;
    }

    public void setMediaListToBaseActivity() {
        mediaList = storage.loadTrackList();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void isStopService(boolean isStop){
        this.mIsStopMusic = isStop;
    }

    public void stopUpdateSeekbar() {
        mHandler.removeCallbacks(mUpdatePositionRunnable);
        mHandler.removeCallbacksAndMessages(null);
    }

}
