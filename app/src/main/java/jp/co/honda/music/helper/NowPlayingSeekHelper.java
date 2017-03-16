package jp.co.honda.music.helper;

import android.media.MediaPlayer;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.SeekBar;
import android.widget.TextView;

import jp.co.honda.music.logger.Logger;
import jp.co.honda.music.service.MediaPlayerService;
import jp.co.honda.music.util.PlayerUtils;

/**
 * @Author: Hoang Vu
 * @Date: 2017/03/05
 */
public class NowPlayingSeekHelper implements OnTouchListener {

    private final static Logger log = new Logger(NowPlayingSeekHelper.class.getSimpleName(), true);
    private final int SEEK_JUMP = 5000;
    private final Runnable mUpdatePositionRunnable;
    private final Handler mHandler;
    private final Handler mSeekHandler = new Handler();

    public View mNextButton;
    public View mPrevButton;
    private Integer mCurrentPosition;

    private MediaPlayerService mPlaybackService;

    private boolean mSeeking = false;
    SeekEventCallback mSeekEventCallback;

    public boolean isSeeking() {
        return mSeeking;
    }

    public void setSeeking(boolean seeking) {
        mSeeking = seeking;
    }

    public Handler getSeekHandler() {
        return mSeekHandler;
    }

    public MediaPlayerService getPlaybackService() {
        return mPlaybackService;
    }

    private final Runnable mSeekForwardRunnable = new Runnable() {
        @Override
        public void run() {
            log.d("Seek forward");
            mSeekEventCallback.updateActionHold(SEEK_JUMP);
        }
    };

    private final Runnable mSeekBackwardRunnable = new Runnable() {
        @Override
        public void run() {

            log.d("Seek backward");
            mSeekEventCallback.updateActionHold(-SEEK_JUMP);
        }
    };

    public NowPlayingSeekHelper(Runnable updatePositionRunnable,
                                Handler handler, View nextButton, View prevButton,
                                MediaPlayerService playbackService,
                                SeekEventCallback seekEventCallback) {
        super();
        log.d("NowPlayingSeekHelper constructor");
        mUpdatePositionRunnable = updatePositionRunnable;
        mHandler = handler;
        mNextButton = nextButton;
        mPrevButton = prevButton;
        mPlaybackService = playbackService;
        mSeekEventCallback = seekEventCallback;

        if (mPrevButton != null) {
            mPrevButton.setOnTouchListener(this);
        }

        if (mNextButton != null) {
            mNextButton.setOnTouchListener(this);
        }

    }

    public static class SeekEventCallback {

        public void onActionUp() {
            // TODO Auto-generated method stub

        }

        public void updateActionHold(int seekJump) {
            // TODO Auto-generated method stub

        }

    }

    public void onTouchForward(View v, int action) {
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                log.d("next ACTION_DOWN");
                mHandler.removeCallbacks(mUpdatePositionRunnable);
                mSeekHandler.postDelayed(mSeekForwardRunnable, 1000);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                log.d("next ACTION_UP");
                mSeekHandler.removeCallbacks(mSeekForwardRunnable);
                mSeekEventCallback.onActionUp();
                clearCurrentPosition();
                break;
        }
    }

    public void onTouchBackward(View v, int action) {
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                log.d("previous ACTION_DOWN");
                mHandler.removeCallbacks(mUpdatePositionRunnable);
                mSeekHandler.postDelayed(mSeekBackwardRunnable, 1000);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                log.d("previous ACTION_UP");
                clearCurrentPosition();
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        if (v == mNextButton) {
            onTouchForward(v, action);
        } else if (v == mPrevButton) {
            onTouchBackward(v, action);
        }

        return false;
    }


    public void setPlaybackService(MediaPlayerService playbackService) {
        this.mPlaybackService = playbackService;
    }

    public void removeHandlerCallbacks() {
        mSeekHandler.removeCallbacks(mSeekForwardRunnable);
    }


    public void postDelayForward() {
        mSeekHandler.postDelayed(mSeekForwardRunnable, 1000);
    }

    public void postDelayBackward() {
        mSeekHandler.postDelayed(mSeekBackwardRunnable, 1000);
    }

    public static void updateProgressBarsOnSeek(int seekJump, NowPlayingSeekHelper helper,
                                                TextView mElapsedTime, TextView mRemainingTime, SeekBar mSeekbar) {

        helper.setSeeking(true);
        helper.removeHandlerCallbacks();

        log.d("updateProgressBarsOnSeek");
        if (helper.getPlaybackService() != null) {

            log.d("updateProgressBarsOnSeek with playback service");
            MediaPlayer ap = helper.getPlaybackService().getMediaPlayer();
            if (ap == null) {
                log.d("MediaPlayer is null");
                return;
            }

            if (helper.mCurrentPosition == null)
                helper.mCurrentPosition = ap.getCurrentPosition();
            int curPos = helper.mCurrentPosition;
            curPos = curPos + seekJump;

            if (curPos < 0) {
                curPos = 0;
            }

            helper.mCurrentPosition = curPos;
            helper.getPlaybackService().seekTo(curPos);

            if (mElapsedTime != null && mRemainingTime != null) {

                log.d("Current position " + curPos);
                String minutes = PlayerUtils.getTimeHoursMinutesSecondsString(curPos);
                log.d("Current position MM:ss " + minutes);
                mElapsedTime.setText(PlayerUtils.getTimeHoursMinutesSecondsString(curPos));
                mRemainingTime.setText("-" + PlayerUtils.getTimeHoursMinutesSecondsString(mSeekbar.getMax() - curPos));
            }

            if (mSeekbar != null) {
                mSeekbar.setProgress(curPos);
            }

            if (seekJump > 0) {
                helper.postDelayForward();
            } else {
                if (curPos > 0) {
                    helper.postDelayBackward();
                } else {
                    helper.onTouchBackward(null, MotionEvent.ACTION_UP);
                }
            }
        }

    }

    private void clearCurrentPosition() {
        mCurrentPosition = null;
    }
}
