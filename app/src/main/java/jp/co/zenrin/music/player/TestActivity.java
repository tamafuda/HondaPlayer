package jp.co.zenrin.music.player;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import jp.co.zenrin.music.common.HondaConstants;

public class TestActivity extends BaseActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }
        Button btn = (Button) findViewById(R.id.test_id_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iController = new Intent(getBaseContext(), PlayScreenActivity.class);
                iController.putExtra("MainActivity", true);
                startActivity(iController);
            }
        });
        //setContentView(R.layout.activity_test);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_test;
    }

    @Override
    protected String detectedScreen() {
        return HondaConstants.DETECTED_SCREEN_IPOD;
    }
}
