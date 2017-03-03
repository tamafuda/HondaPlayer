package jp.co.zenrin.music.player;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Spinner;

import java.util.ArrayList;

import jp.co.zenrin.music.fragment.AMFMFragment;
import jp.co.zenrin.music.fragment.IPodFragment;
import jp.co.zenrin.music.model.SpinnerNavItem;
import jp.co.zenrin.music.model.TitleNavigationAdapter;

public class TestFragment extends AppCompatActivity {

    private Toolbar mToolbar;
    private Spinner mSpinner;
    // Navigation adapter
    private TitleNavigationAdapter spinAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_fragment);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mSpinner = (Spinner) findViewById(R.id.spinner_nav);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }
        addItemsToSpinner();
    }
    // add items into spinner dynamically
    public void addItemsToSpinner() {
        SpinnerNavItem spn;
        ArrayList<SpinnerNavItem> list = new ArrayList<SpinnerNavItem>();
        spn = new SpinnerNavItem("iPod", R.drawable.ipod);
        list.add(spn);
        spn = new SpinnerNavItem("InternetAudio", R.drawable.internet_audio);
        list.add(spn);
        spn = new SpinnerNavItem("FA/AM", R.drawable.fm_am);
        list.add(spn);
        spn = new SpinnerNavItem("Bluetooth", R.drawable.bluetooth);
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
        Fragment fr =  null;
        if (position == 0){
            fr = new IPodFragment();
        }else if (position == 1) {
            fr = new AMFMFragment();
        }
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fr);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}
