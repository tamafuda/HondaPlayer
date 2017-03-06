package jp.co.honda.music.player;


import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import jp.co.honda.music.logger.Logger;


/**
 * @Author: Hoang Vu
 * @Date:   2017/02/23
 */

public class MainActivity extends BaseActionBarActivity {

    // Logger
    protected final Logger log = new Logger(MainActivity.class.getSimpleName(), true);
    // Controller button
    Button mController;
    Button mPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log.d("onCreate");
        //setContentView(R.layout.activity_main);
        // Change title
        //getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        //getSupportActionBar().setDisplayShowCustomEnabled(true);
        //getSupportActionBar().setCustomView(R.layout.custom_action_bar_text);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }
        mController = (Button) findViewById(R.id.bt_controller);
        mPlay = (Button) findViewById(R.id.btn_play);
        // Add event button
        mController.setOnClickListener(mOnclick);
        mPlay.setOnClickListener(mOnclick);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main;
    }

    @Override
    protected String detectedScreen() {
        return "";
    }

    /**
     *
     * @param v
     */
    private View.OnClickListener mOnclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final int id = v.getId();
            switch (id) {
                case R.id.bt_controller:
                    //Intent iController = new Intent(getBaseContext(), ControllerActivity.class);
                   /* Intent iController = new Intent(getBaseContext(), TestActivity.class);
                    iController.putExtra("MainActivity", true);
                    startActivity(iController);*/
                    break;
                case R.id.btn_play:
                    /*Intent iPlay = new Intent(getBaseContext(), PlayScreenActivity.class);
                    //Intent iPlay = new Intent(getBaseContext(), PlayMediaActivity.class);
                    iPlay.putExtra("MainActivity", true);
                    startActivity(iPlay);*/
                    break;
                // even more buttons here
            }
        }
    };

}
