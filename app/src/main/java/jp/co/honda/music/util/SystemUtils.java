package jp.co.honda.music.util;

import android.os.Build;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author: Hoang Vu
 * @Date: 2017/02/25
 */

public class SystemUtils {

    /**
     * Get device infor such as device name, product name
     * @return device info
     */
    public static String getDeviceInfo() {
        String branName = Build.MANUFACTURER;      // Manufacturer will come I think, Correct me if I am wrong :)  Brand name like Samsung or Mircomax
        String myDeviceModel = android.os.Build.MODEL;
        String deviceName = android.os.Build.DEVICE;           // Device
        String deviceModel = android.os.Build.MODEL;            // Model
        String productName = android.os.Build.PRODUCT;
        String returnString = branName + " " + deviceModel;
        if (returnString.length() < 2) {
            return "No device name";
        }
        return returnString;
    }

    /**
     * Get system date time
     * @return
     */
    public static String getSystemTimeNotify() {
        Date dt = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
        String dateTimeNotify = sdf.format(dt);
        return dateTimeNotify;
    }
}
