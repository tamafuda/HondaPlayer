package jp.co.zenrin.music.player;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import jp.co.zenrin.music.dialog.PopupUtils;

/**
 * @Author: Hoang Vu
 * @Date:   2017/02/23
 */

public class RadioPlayer extends AppCompatActivity {

    //private PopupWindow mPopupWindow;
    //private RelativeLayout mRelativeLayout;

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
        // Add back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get the widgets reference from XML layout
        //mRelativeLayout = (RelativeLayout) findViewById(R.id.id_activity_radio);

        Intent intent = getIntent();
        boolean iGet = intent.getBooleanExtra("Broadcast",false);
        if(iGet) {
            Toast.makeText(getBaseContext(),"OK", Toast.LENGTH_SHORT).show();
            PopupUtils popupUtils = new PopupUtils(this);
            popupUtils.notifyDialogCustom(R.string.popup_notify_content);
        }
    }



    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onNavigateUp() {
        return super.onNavigateUp();
    }

}
