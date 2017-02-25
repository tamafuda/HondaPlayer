package jp.co.zenrin.music.player;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import jp.co.zenrin.music.zdccore.Logger;

/**
 * @Author: Hoang Vu
 * @Date:   2017/02/23
 */

public class ControllerActivity extends AppCompatActivity{
    // Logger
    protected final Logger log = new Logger(ControllerActivity.class.getSimpleName(), true);

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

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Toast.makeText(getApplicationContext(), "Tap on arrow", Toast.LENGTH_SHORT);
    }


}
