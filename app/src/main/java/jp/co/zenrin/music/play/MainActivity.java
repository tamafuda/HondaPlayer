package jp.co.zenrin.music.play;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


/**
 * @Author : v_hoang
 */
public class MainActivity extends AppCompatActivity {

    // Controller button
    Button mController;
    Button mPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Change title
        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar_text);

        mController = (Button) findViewById(R.id.bt_controller);
        mPlay = (Button) findViewById(R.id.btn_play);
        // Add event button
        mController.setOnClickListener(mOnclick);
        mPlay.setOnClickListener(mOnclick);



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
                    Intent iController = new Intent(getBaseContext(), ControllerActivity.class);
                    iController.putExtra("MainActivity", true);
                    startActivity(iController);
                    break;
                case R.id.btn_play:
                    Intent iPlay = new Intent(getBaseContext(), PlayScreenActivity.class);
                    iPlay.putExtra("MainActivity", true);
                    startActivity(iPlay);
                    break;
                // even more buttons here
            }
        }
    };

}
