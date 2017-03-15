package jp.co.honda.music.player;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.content.ContextCompat;
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

import jp.co.honda.music.adapter.TitleNavigationAdapter;
import jp.co.honda.music.common.HondaConstants;
import jp.co.honda.music.dialog.PopupUtils;
import jp.co.honda.music.fragment.AMFMFragment;
import jp.co.honda.music.fragment.IPodFragment;
import jp.co.honda.music.fragment.InternetRadioFragment;
import jp.co.honda.music.logger.Logger;
import jp.co.honda.music.model.SpinnerNavItem;
import jp.co.honda.music.notification.AIRecommendReceiver;
import jp.co.honda.music.service.DialogNotifyService;
import jp.co.honda.music.service.MediaPlayerService;
import jp.co.honda.music.zdccore.HondaSharePreference;

public class HomeBaseFragment extends BasePlayerActivity implements View.OnClickListener{
    protected final Logger log = new Logger(HomeBaseFragment.class.getSimpleName(), true);
    private Toolbar mToolbar;
    private Spinner mSpinner;
    // Navigation adapter
    private TitleNavigationAdapter spinAdapter;

    private GestureDetector gestureDetector;
    private int detectScreen = 0;
    private ImageButton mPlay;
    private ImageButton mPause;
    private HondaSharePreference storage;
    private boolean isRecreate;
    private Dialog mDialog;

    private IntentFilter mIntentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log.d("onCreate");
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_test_fragment);
        // Setup notification

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(HondaConstants.BROADCAST_SHOW_POPUP);
        Intent serviceIntent = new Intent(this, DialogNotifyService.class);
        startService(serviceIntent);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            boolean isRadar = extras.getBoolean(HondaConstants.DETECTED_SCREEN_CAPSUL,false);
            if(isRadar) {
                super.stopUpdateSeekbar();
                stopService(new Intent(HomeBaseFragment.this, MediaPlayerService.class));
            }
        }

        if (savedInstanceState == null) {
            selectFrag(0);
        }
        setupNotification();
        storage = new HondaSharePreference(this);

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
                    HomeBaseFragment.super.stopUpdateSeekbar();
                    if(detectScreen == 0 || detectScreen == 3) {
                        stopService(new Intent(HomeBaseFragment.this, MediaPlayerService.class));
                    }
                    Intent iPlay = new Intent(getBaseContext(), RadarMusicActivity.class);
                    iPlay.putExtra(HondaConstants.DETECTED_SCREEN_FLING, true);
                    startActivity(iPlay);
                    finish();
                    return true;

                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }
        });

        //mPlay = (ImageButton) findViewById(R.id.btn_play);
        //mPause = (ImageButton) findViewById(R.id.btn_pause);
        //mPlay.setOnClickListener(this);
        //mPause.setOnClickListener(this);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(HondaConstants.BROADCAST_SHOW_POPUP)) {
                if (detectScreenID().equals(HondaConstants.DETECT_FRAGMENT_NETRADIO)) {
                    PopupUtils popupUtils = new PopupUtils(HomeBaseFragment.this);
                    mDialog = popupUtils.notifyDialogCustom(HomeBaseFragment.this, R.string.popup_notify_content);
                    mDialog.show();
                }else {
                    stopService(new Intent(HomeBaseFragment.this, DialogNotifyService.class));
                    startService(new Intent(HomeBaseFragment.this, DialogNotifyService.class));
                }

            }
        }
    };

    @Override
    protected void onDestroy() {
        stopService(new Intent(this,DialogNotifyService.class));
        super.onDestroy();

    }

    @Override
    protected void onPause() {
        unregisterReceiver(mReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    protected int getLayoutResourceId() {
        //setContentView(R.layout.activity_test_fragment);
        return R.layout.activity_test_fragment;
    }

    @Override
    protected int getAudioIndex() {
        return 0;
    }

    @Override
    protected boolean isNeedKeepMediaSrv() {
        return false;
    }


    // add items into spinner dynamically
    public void addItemsToSpinner() {
        SpinnerNavItem spn;
        final ArrayList<SpinnerNavItem> list = new ArrayList<SpinnerNavItem>();
        spn = new SpinnerNavItem("FM/AM", R.drawable.fm_am, true);
        list.add(spn);
        spn = new SpinnerNavItem("iPod", R.drawable.ipod,false);
        list.add(spn);
        spn = new SpinnerNavItem("Bluetooth", R.drawable.bluetooth,false);
        list.add(spn);
        spn = new SpinnerNavItem("InternetRadio", R.drawable.internet_audio,false);
        list.add(spn);

        // Custom ArrayAdapter with spinner item layout to set popup background
        spinAdapter = new TitleNavigationAdapter(
                getApplicationContext(), list);
        mSpinner.setAdapter(spinAdapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            boolean isCheck = false;

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //TextView v = (TextView) view.findViewById(R.id.txtTitle);
                //v.setTextColor(ContextCompat.getColor(getBaseContext(),R.color.colorYellow));
                storage.storeSpinnerItemSelected(position);
                String item = parent.getItemAtPosition(position).toString();
                Log.d("TEST", String.valueOf(position));
                selectFrag(position);
                HomeBaseFragment.super.initScreen();
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
            fr = new IPodFragment();
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
        super.stopUpdateSeekbar();
        stopService(new Intent(HomeBaseFragment.this, MediaPlayerService.class));
        super.isStopService(true);
        this.finish();
        //super.onDestroy();
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

    public void playMusicInList(int pos) {
        log.d("Play when user touch on listview ");
        storage.storeTrackIndex(pos);
        super.playFromAdapter();
    }

    public void setMediaListToBaseActivity() {
        super.setMediaListToBaseActivity();
    }

    private String detectFragment(int detect) {
        log.d("DetectFragment");
        String returnScreen = "";
        switch (detect) {
            case 0:
                returnScreen = HondaConstants.DETECT_FRAGMENT_FMAM;
                break;
            case 1:
                returnScreen = HondaConstants.DETECT_FRAGMENT_IPOD;
                break;
            case 2:
                returnScreen = HondaConstants.DETECT_FRAGMENT_IPOD;
                break;
            case 3:
                returnScreen = HondaConstants.DETECT_FRAGMENT_NETRADIO;
                break;
            default:
                returnScreen = HondaConstants.DETECT_FRAGMENT_FMAM;
        }
        return returnScreen;
    }

    @Override
    protected String detectScreenID() {
        log.d("detectScreenID");
        return detectFragment(detectScreen);
    }

    public boolean detectPopupWindow() {
        if(detectScreenID().equals(HondaConstants.DETECT_FRAGMENT_FMAM)
                || detectScreenID().equals(HondaConstants.DETECT_FRAGMENT_NETRADIO)) {
            return true;
        }
        return false;
    }


}

