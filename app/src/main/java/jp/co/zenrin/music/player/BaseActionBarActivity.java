package jp.co.zenrin.music.player;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import java.util.ArrayList;

import jp.co.zenrin.music.common.HondaConstants;
import jp.co.zenrin.music.model.SpinnerNavItem;
import jp.co.zenrin.music.model.TitleNavigationAdapter;

public abstract class BaseActionBarActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private Spinner mSpinner;
    // Navigation adapter
    private TitleNavigationAdapter spinAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mSpinner = (Spinner) findViewById(R.id.spinner_nav);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
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
                if (position == 0 && !detectedScreen().equals(HondaConstants.DETECTED_SCREEN_IPOD)) {
                    Intent iController = new Intent(getBaseContext(), TestActivity.class);
                    iController.putExtra("MainActivity", true);
                    startActivity(iController);
                }else if (position == 1 && !detectedScreen().equals(HondaConstants.DETECTED_SCREEN_INTERNET_AUDIO)) {
                    Intent iController = new Intent(getBaseContext(), TestActivity.class);
                    iController.putExtra("MainActivity", true);
                    startActivity(iController);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
    // === ABSTRACT METHOD ====
    protected abstract int getLayoutResourceId();
    protected abstract String detectedScreen();

}
