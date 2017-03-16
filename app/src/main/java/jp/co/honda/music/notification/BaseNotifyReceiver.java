package jp.co.honda.music.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @Author: Hoang Vu
 * @Date: 2017/02/25
 * This class will be called when system is changed
 * It defines in AndroidManifest.xml file
 */

public abstract class BaseNotifyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

    }
}
