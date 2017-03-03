package jp.co.zenrin.music.player;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import jp.co.zenrin.music.common.HondaConstants;
import jp.co.zenrin.music.util.CheckSystemPermissions;

/**
 * @Author: Hoang Vu
 * @Date:   2017/02/23
 */

public class PlayScreenActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "PlayScreenActivity";
    private Button mBtnArrange;
    private Button mAIRecommend;
    private View mView;
    private boolean isPermission = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_screen);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }
        // Change title
        //getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        //getSupportActionBar().setDisplayShowCustomEnabled(true);
        //getSupportActionBar().setCustomView(R.layout.custom_action_bar_text);
        //View view =getSupportActionBar().getCustomView();
        //TextView txtView = (TextView) view.findViewById(R.id.title_action_bar);
        //txtView.setText(getResources().getString(R.string.txt_play));
        // Add back button
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Arrange button
        mBtnArrange = (Button) findViewById(R.id.btn_arrange);
        mBtnArrange.setOnClickListener(this);

        // AI recommend button
        mAIRecommend = (Button) findViewById(R.id.btn_ai_recommend);
        mAIRecommend.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        mView = view;
        int id = view.getId();

        Intent intent = null;
        switch (id) {
            case R.id.btn_arrange:
                intent = new Intent(PlayScreenActivity.this, ArrangeActivity.class);
                intent.putExtra(TAG,true);
                break;
            case R.id.btn_ai_recommend:
                if (CheckSystemPermissions.checkPermission(getApplicationContext(), HondaConstants.READ_EXTERNAL_STORAGE)) {
                    isPermission = true;
                    Snackbar.make(mView, "Permission already granted.", Snackbar.LENGTH_INDEFINITE).show();
                }else {
                    CheckSystemPermissions.requestPermission(this,getApplicationContext(),HondaConstants.READ_EXTERNAL_STORAGE);
                }
                if (isPermission) {
                    intent = new Intent(PlayScreenActivity.this, MusicPlayActivity.class);
                    intent.putExtra(TAG, true);
                    break;
                }
        }

        if (intent != null) {
            startActivity(intent);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case HondaConstants.PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Snackbar.make(mView,"Permission Granted, Now you can access location data.", Snackbar.LENGTH_LONG).show();
                    isPermission = true;

                } else {
                    isPermission = false;
                    Snackbar.make(mView,"Permission Denied, You cannot access location data.",Snackbar.LENGTH_LONG).show();
                }
                break;
        }
    }
}
