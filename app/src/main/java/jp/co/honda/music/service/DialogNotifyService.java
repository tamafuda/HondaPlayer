package jp.co.honda.music.service;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import jp.co.honda.music.common.HondaConstants;
import jp.co.honda.music.logger.Logger;

/**
 * @Author: Hoang Vu
 * @Date:   2017/03/10
 * Notification Service is used to send broadcast every 1 minutes
 * that received at only InternetRadio screen
 *
 */
public class DialogNotifyService extends Service {

    protected static final Logger log = new Logger(DialogNotifyService.class.getSimpleName(), true);
    public DialogNotifyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        log.d("onBind");
        return null;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        log.d("onTaskRemoved");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        log.d("onDestroy");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(HondaConstants.BROADCAST_SHOW_POPUP);
                broadcastIntent.putExtra(HondaConstants.BROADCAST_SHOW_POPUP, true);
                sendBroadcast(broadcastIntent);
            }
        }, HondaConstants.POPUP_SHOW_TIMER);
        return START_REDELIVER_INTENT;
    }
}
