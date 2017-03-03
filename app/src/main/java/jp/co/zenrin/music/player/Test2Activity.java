package jp.co.zenrin.music.player;

import android.os.Bundle;

import jp.co.zenrin.music.common.HondaConstants;

public class Test2Activity extends BaseActionBarActivity {
    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_test2;
    }

    @Override
    protected String detectedScreen() {
        return HondaConstants.DETECTED_SCREEN_INTERNET_AUDIO;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
