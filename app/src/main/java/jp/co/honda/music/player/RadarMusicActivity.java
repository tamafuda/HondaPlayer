package jp.co.honda.music.player;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import jp.co.honda.music.common.HondaConstants;
import jp.co.honda.music.dialog.ProgressDialogTask;
import jp.co.honda.music.logger.Logger;

/**
 * @Author: Hoang Vu
 * @Date:   2017/03/01
 * Arrange screen , download music, navigation
 *
 */
public class RadarMusicActivity extends BasePlayerActivity{

    // Logger
    protected final Logger log = new Logger(RadarMusicActivity.class.getSimpleName(), true);

    private TextView mTitle;
    private Button mAIRecommend;
    private Button mDownload1;
    private Button mDownload2;
    private Button mDownload3;
    private Button mMusic1;
    private Button mMusic2;
    private Button mMusic3;
    private ImageView mScreenBg;
    private Context context;
    private GestureDetector gestureDetector;
    private int receiveDetectSrc = 0;
    private boolean detectFling = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_radar_music);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            detectFling = extras.getBoolean(HondaConstants.DETECTED_SCREEN_FLING);
        }

        mTitle = (TextView) findViewById(R.id.id_common_title);
        mTitle.setText("Music プレィモード");


        mAIRecommend = (Button) findViewById(R.id.ai_recommend);
        mScreenBg = (ImageView) findViewById(R.id.id_screen_bg);
        mDownload1 = (Button) findViewById(R.id.download_music1);
        mMusic1 = (Button) findViewById(R.id.ic_music1);
        mDownload2 = (Button) findViewById(R.id.download_music2);
        mMusic2 = (Button) findViewById(R.id.ic_music2);
        mDownload3 = (Button) findViewById(R.id.download_music3);
        mMusic3 = (Button) findViewById(R.id.ic_music3);
        // Can not set one view with 2 event listener
        // Ex : screenBg with onClickListener and onTouchListener
        // Should be prepare each button music to handle
        mScreenBg.setOnClickListener(mOnclick);
        mAIRecommend.setOnClickListener(mOnclick);
        mMusic1.setOnClickListener(mOnclick);
        mMusic2.setOnClickListener(mOnclick);
        mMusic3.setOnClickListener(mOnclick);
        mDownload1.setOnClickListener(mOnclick);
        mDownload2.setOnClickListener(mOnclick);
        mDownload3.setOnClickListener(mOnclick);

        // Fling screen
        //View v = findViewById(R.id.activity_radar_music);
        mScreenBg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e1.getX() - e2.getX() < 50) {
                    RadarMusicActivity.super.releaseMediaPlayer();
                    //Toast.makeText(getBaseContext(), "SwipLeft", Toast.LENGTH_SHORT).show();
                    Intent iPlay = new Intent(getBaseContext(), HomeBaseFragment.class);
                    iPlay.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //Intent iPlay = new Intent(getBaseContext(), PlayMediaActivity.class);
                    iPlay.putExtra(HondaConstants.DETECTED_SCREEN_CAPSUL, true);
                    startActivity(iPlay);
                    finish();
                    //startActivity(iPlay);
                    return true;

                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }

            @Override
            public boolean onDown(MotionEvent e) {
                log.d(String.valueOf(e.getAction()));
                log.d(String.valueOf(e.getX()));
                log.d(String.valueOf(e.getY()));
                return true;
                /*if (detectFling){
                    return true;
                }
                return false;*/
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return super.onSingleTapUp(e);
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                return super.onSingleTapConfirmed(e);
            }

            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                return super.onDoubleTap(e);
            }
        });

    }

    @Override
    protected int getLayoutResourceId() {
        return 0;
    }

    @Override
    protected int getAudioIndex() {
        return 0;
    }

    @Override
    protected boolean isNeedKeepMediaSrv() {
        return false;
    }

    @Override
    protected String detectScreenID() {
        return HondaConstants.DETECTED_SCREEN_CAPSUL;
    }

    private View.OnClickListener mOnclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final int id = v.getId();
            int resoureId = 0;
            switch (id) {
                case R.id.ai_recommend:
                    //releaseMediaPlayer();
                    Intent iPlay = new Intent(getBaseContext(), AIMixAudio.class);
                    iPlay.putExtra("AIMusicPlaying", true);
                    startActivity(iPlay);
                    //overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                    //overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                    finish();
                    break;
                case R.id.download_music1:
                    ProgressDialogTask task1 = new ProgressDialogTask(RadarMusicActivity.this, true, R.string.popup_downloading);
                    task1.execute(0);
                    mDownload1.setVisibility(View.GONE);
                    break;
                case R.id.download_music2:
                    ProgressDialogTask task2 = new ProgressDialogTask(RadarMusicActivity.this, true, R.string.popup_downloading);
                    task2.execute(0);
                    mDownload2.setVisibility(View.GONE);
                    break;
                case R.id.download_music3:
                    ProgressDialogTask task3 = new ProgressDialogTask(RadarMusicActivity.this, true, R.string.popup_downloading);
                    task3.execute(0);
                    mDownload3.setVisibility(View.GONE);
                    break;
                case R.id.id_screen_bg:
                    mDownload1.setVisibility(View.VISIBLE);
                    break;
                case R.id.ic_music1:
                    mDownload1.setVisibility(View.VISIBLE);
                    break;
                case R.id.ic_music2:
                    mDownload2.setVisibility(View.VISIBLE);
                    break;
                case R.id.ic_music3:
                    mDownload3.setVisibility(View.VISIBLE);
                    break;
                // even more buttons here
            }
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onBackPressed() {
        super.releaseMediaPlayer();
        Intent iPlay = new Intent(getBaseContext(), HomeBaseFragment.class);
        iPlay.putExtra(HondaConstants.DETECTED_SCREEN_CAPSUL, true);
        startActivity(iPlay);
        finish();
    }
}
