package jp.co.zenrin.music.util;

import android.os.Build;

/**
 * @Author: Hoang Vu
 * @Date: 2017/03/03
 */

public class SystemUtils {
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
}
