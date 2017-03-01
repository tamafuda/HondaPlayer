package jp.co.zenrin.music.player;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

            // custom dialog
            // Should be use "this"
            // getApplicationContext() or getBaseContext() is false
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.notify_pupup_custom);
            dialog.setCancelable(false);
            Button btn = (Button) dialog.findViewById(R.id.close_popup);
            btn.setText(R.string.popup_notify_content);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            dialog.getWindow().setGravity(Gravity.TOP);
            dialog.show();
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
