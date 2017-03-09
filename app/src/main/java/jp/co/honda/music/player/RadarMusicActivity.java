package jp.co.honda.music.player;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import jp.co.honda.music.common.HondaConstants;
import jp.co.honda.music.dialog.ProgressDialogTask;
import jp.co.honda.music.logger.Logger;

public class RadarMusicActivity extends AppCompatActivity {

    // Logger
    protected final Logger log = new Logger(RadarMusicActivity.class.getSimpleName(), true);

    TextView mTitle;
    Button mAIRecommend;
    Button mDownload;
    Button mMusic;
    ImageView mScreenBg;
    Context context;

    private GestureDetector gestureDetector;

    private int receiveDetectSrc = 0;
    boolean detectFling = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        mDownload = (Button) findViewById(R.id.download_music);
        mScreenBg = (ImageView) findViewById(R.id.id_screen_bg);
        mMusic = (Button) findViewById(R.id.ic_music);
        // Can not set one view with 2 event listener
        // Ex : screenBg with onClickListener and onTouchListener
        // Should be prepare each button music to handle
        mScreenBg.setOnClickListener(mOnclick);
        mDownload.setOnClickListener(mOnclick);
        mAIRecommend.setOnClickListener(mOnclick);
        mMusic.setOnClickListener(mOnclick);

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
                    Toast.makeText(getBaseContext(), "SwipLeft", Toast.LENGTH_SHORT).show();
                    Intent iPlay = new Intent(getBaseContext(), HomeBaseFragment.class);
                    iPlay.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //Intent iPlay = new Intent(getBaseContext(), PlayMediaActivity.class);
                    iPlay.putExtra("MainActivity", true);
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

    private View.OnClickListener mOnclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final int id = v.getId();
            int resoureId = 0;
            switch (id) {
                case R.id.ai_recommend:
                    Intent iPlay = new Intent(getBaseContext(), MusicPlayActivity.class);
                    iPlay.putExtra("MainActivity", true);
                    startActivity(iPlay);
                    //overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                    //overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                    finish();
                    break;
                case R.id.download_music:
                    ProgressDialogTask task = new ProgressDialogTask(RadarMusicActivity.this, true, R.string.popup_downloading);
                    task.execute(0);

                    break;
                case R.id.id_screen_bg:
                    mDownload.setVisibility(View.VISIBLE);
                    break;
                case R.id.ic_music:
                    mDownload.setVisibility(View.VISIBLE);
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
        Intent iPlay = new Intent(getBaseContext(), HomeBaseFragment.class);
        //iPlay.putExtra(HondaConstants.DETECTED_SCREEN_FLING, true);
        startActivity(iPlay);
        finish();
    }
}
