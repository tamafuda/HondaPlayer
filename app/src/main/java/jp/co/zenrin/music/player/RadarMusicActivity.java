package jp.co.zenrin.music.player;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import jp.co.zenrin.music.dialog.ProgressDialogTask;

public class RadarMusicActivity extends AppCompatActivity {

    TextView mTitle;
    Button mAIRecommend;
    Button mDownload;
    ImageView mScreenBg;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radar_music);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }
        context = this.getBaseContext();
        mTitle = (TextView) findViewById(R.id.id_common_title);
        mTitle.setText("Music プレィモード");

        mAIRecommend = (Button) findViewById(R.id.ai_recommend);
        mDownload = (Button) findViewById(R.id.download_music);
        mScreenBg = (ImageView) findViewById(R.id.id_screen_bg);
        mScreenBg.setOnClickListener(mOnclick);
        mDownload.setOnClickListener(mOnclick);
        mAIRecommend.setOnClickListener(mOnclick);

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
                    break;
                case R.id.download_music:
                    ProgressDialogTask task = new ProgressDialogTask(RadarMusicActivity.this, true, R.string.popup_downloading);
                    task.execute(0);

                    break;
                case R.id.id_screen_bg:
                    mDownload.setVisibility(View.VISIBLE);
                    break;
                // even more buttons here
            }
        }
    };

}
