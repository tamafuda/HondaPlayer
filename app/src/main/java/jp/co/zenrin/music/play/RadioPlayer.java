package jp.co.zenrin.music.play;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class RadioPlayer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radio_player);
        // Change title
        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar_text);
        View view =getSupportActionBar().getCustomView();
        TextView txtView = (TextView) view.findViewById(R.id.title_action_bar);
        txtView.setText(getResources().getString(R.string.txt_fm_am));
    }
}
