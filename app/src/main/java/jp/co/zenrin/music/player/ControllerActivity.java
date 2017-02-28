package jp.co.zenrin.music.player;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import jp.co.zenrin.music.notification.AIRecommendReceiver;
import jp.co.zenrin.music.zdccore.Logger;

/**
 * @Author: Hoang Vu
 * @Date:   2017/02/23
 */

public class ControllerActivity extends AppCompatActivity{
    // Logger
    protected final Logger log = new Logger(ControllerActivity.class.getSimpleName(), true);
    Button btnAMFM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log.d("onCreate");
        setContentView(R.layout.activity_controller);
        // Change title
        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar_text);
        View view =getSupportActionBar().getCustomView();
        TextView txtView = (TextView) view.findViewById(R.id.title_action_bar);
        txtView.setText(getResources().getString(R.string.txt_controller));
        // Add back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        btnAMFM = (Button) findViewById(R.id.btn_am_fm);
        btnAMFM.setOnClickListener(mOnclick);

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
                case R.id.btn_am_fm:
                    AIRecommendReceiver.setupNotify(getBaseContext());
                    Intent iRadioPlayer = new Intent(getBaseContext(), RadioPlayer.class);
                    iRadioPlayer.putExtra("ControllerActivity", true);
                    startActivity(iRadioPlayer);
                    break;
                // even more buttons here
            }
        }
    };

}
