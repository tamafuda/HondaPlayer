package jp.co.zenrin.music.player;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;

import jp.co.zenrin.music.common.HondaConstants;
import jp.co.zenrin.music.util.CheckSystemPermissions;
import jp.co.zenrin.music.zdccore.Logger;

import static android.support.v7.widget.StaggeredGridLayoutManager.TAG;

/**
 * @Author: Hoang Vu
 * @Date:   2017/02/23
 */


public class HondaPlayApplication extends Application {
    // Logger
    protected final Logger log = new Logger(HondaPlayApplication.class.getSimpleName(), true);

    @Override
    public void onCreate() {
        super.onCreate();
        log.d("onCreate");

        // Register to be informed of activities starting up
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                // If new activity is created,force its orientation to portrait
                Log.d(TAG, "onActivityCreated");
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            }

            @Override
            public void onActivityStarted(Activity activity) {
                Log.d(TAG, "onActivityStarted");

            }

            @Override
            public void onActivityResumed(Activity activity) {
                Log.d(TAG, "onActivtyResumed");
            }

            @Override
            public void onActivityPaused(Activity activity) {
                Log.d(TAG, "onActivityPaused");
            }

            @Override
            public void onActivityStopped(Activity activity) {
                Log.d(TAG, "onActivityStopped");
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }


}
