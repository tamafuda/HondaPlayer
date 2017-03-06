package jp.co.zenrin.music.player;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import jp.co.zenrin.music.common.HondaConstants;
import jp.co.zenrin.music.dialog.PopupUtils;
import jp.co.zenrin.music.fragment.AMFMFragment;
import jp.co.zenrin.music.fragment.IPodFragment;
import jp.co.zenrin.music.fragment.InternetRadioFragment;
import jp.co.zenrin.music.model.SpinnerNavItem;
import jp.co.zenrin.music.adapter.TitleNavigationAdapter;
import jp.co.zenrin.music.notification.AIRecommendReceiver;
import jp.co.zenrin.music.logger.Logger;

public class HomeBaseFragment extends AppCompatActivity implements View.OnClickListener{
    protected final Logger log = new Logger(MusicPlayActivity.class.getSimpleName(), true);
    private Toolbar mToolbar;
    private Spinner mSpinner;
    // Navigation adapter
    private TitleNavigationAdapter spinAdapter;

    private GestureDetector gestureDetector;
    private int detectScreen = 0;
    private ImageButton mPlay;
    private ImageButton mPause;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log.d("onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_fragment);
        // Setup notification
        setupNotification();

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mSpinner = (Spinner) findViewById(R.id.spinner_nav);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }
        addItemsToSpinner();

        LinearLayout v = (LinearLayout) findViewById(R.id.frame_layout);
        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e1.getX() - e2.getX() > 50) {
                    Toast.makeText(getBaseContext(), "SwipLeft", Toast.LENGTH_SHORT).show();
                    Intent iPlay = new Intent(getBaseContext(), RadarMusicActivity.class);
                    iPlay.putExtra(HondaConstants.DETECTED_SCREEN_FLING, true);
                    startActivity(iPlay);
                    //finish();
                    return true;

                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }
        });

        mPlay = (ImageButton) findViewById(R.id.btn_play);
        mPause = (ImageButton) findViewById(R.id.btn_pause);
        mPlay.setOnClickListener(this);
        mPause.setOnClickListener(this);
    }



    // add items into spinner dynamically
    public void addItemsToSpinner() {
        SpinnerNavItem spn;
        ArrayList<SpinnerNavItem> list = new ArrayList<SpinnerNavItem>();
        spn = new SpinnerNavItem("FM/AM", R.drawable.fm_am);
        list.add(spn);
        spn = new SpinnerNavItem("iPod", R.drawable.ipod);
        list.add(spn);
        spn = new SpinnerNavItem("Bluetooth", R.drawable.bluetooth);
        list.add(spn);
        spn = new SpinnerNavItem("InternetRadio", R.drawable.internet_audio);
        list.add(spn);

        // Custom ArrayAdapter with spinner item layout to set popup background
        spinAdapter = new TitleNavigationAdapter(
                getApplicationContext(), list);
        mSpinner.setAdapter(spinAdapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            boolean isCheck = false;

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                Log.d("TEST", String.valueOf(position));
                selectFrag(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void selectFrag(int position) {
        Fragment fr = null;
        // FM/AM
        if (position == 0) {
            fr = new AMFMFragment();
            detectScreen = position;

        // iPod
        } else if (position == 1) {
            fr = new IPodFragment();
            detectScreen = position;

         // Bluetooth
        } else if (position == 2) {
            fr = new AMFMFragment();
            detectScreen = position;
        } else if (position == 3) {
            fr = new InternetRadioFragment();
            detectScreen = position;
        }

         // Internet Audio
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fr);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void setSelection(int selection) {
        mSpinner.setSelection(selection,true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        log.d("Debug");
        this.finish();
        //this.onDestroy();
        //super.onBackPressed();
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
    }

    private void setupNotification() {
        AIRecommendReceiver.setupNotify(getBaseContext());
    }

    private void receiverNotify() {
        Intent intent = getIntent();
        boolean iGet = intent.getBooleanExtra(HondaConstants.BROADCAST_AI_RECOMMEND,false);
        if(iGet) {
            PopupUtils popupUtils = new PopupUtils(this);
            //popupUtils.notifyDialogCustom(R.string.popup_notify_content);
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_play:
                mPlay.setVisibility(View.GONE);
                mPause.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_pause:
                mPlay.setVisibility(View.VISIBLE);
                mPause.setVisibility(View.GONE);
                break;
        }
    }
}

