package jp.co.zenrin.music.play;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import jp.co.zenrin.music.player.ArrangeActivity;
import jp.co.zenrin.music.player.MusicArrangeActivity;
import jp.co.zenrin.music.player.R;
import jp.co.zenrin.music.service.MusicService;
import jp.co.zenrin.music.zdccore.ArrangeAdapter;

public class PlayScreenActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "PlayScreenActivity";
    private Button mBtnArrange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_screen);
        // Change title
        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar_text);
        View view =getSupportActionBar().getCustomView();
        TextView txtView = (TextView) view.findViewById(R.id.title_action_bar);
        txtView.setText(getResources().getString(R.string.txt_play));

        mBtnArrange = (Button) findViewById(R.id.btn_arrange);
        mBtnArrange.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        Intent intent = null;
        switch (id) {
            case R.id.btn_arrange:
                intent = new Intent(PlayScreenActivity.this, ArrangeActivity.class); // =>  demo of Mr Hoang
                intent.putExtra(TAG,true);
                break;
        }

        if (intent != null) {
            startActivity(intent);
        }

    }
}
