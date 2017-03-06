package jp.co.honda.music.zdccore;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

/**
 * /**
 * @Author: Hoang Vu
 * @Date:   2017/02/23
 *
 * Android Marshmallow 6.0 final stable version is released a few weeks back.
 * Android Marshmallow brought some new API changes
 * and one of the most important API change is the addition of new granular permissions.
 * Lets dive into the new permission system
 */

public final class CheckSystemPermissions {

    private static final int PERMISSION_REQUEST_CODE = 1;

    /**
     *
     * @param context
     * @param permissionName
     * @return true OR false
     *
     * Example : If we want to check " Manifest.permission.ACCESS_FINE_LOCATION", set parameter is that string
     * Only check on SDK >=23 - Please read google document in this case
     */
    public static boolean checkPermission(Context context, String permissionName) {
        if (Build.VERSION.SDK_INT >= 23) {
            int result = ContextCompat.checkSelfPermission(context, permissionName);
            if (result == PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }
        return false;
    }


    public static void requestPermissionActivity(Activity activity, Context context, String permissionName){

        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permissionName)){

            Toast.makeText(context,"Android 6.0 needs permission allows us to access media data. Please allow in App Settings for additional functionality.",Toast.LENGTH_LONG).show();

        } else {
            ActivityCompat.requestPermissions(activity,new String[]{permissionName},PERMISSION_REQUEST_CODE);
        }
    }

    public static void requestPermissionFragment(Fragment fragment, Context context, String permissionName){

    }
}
