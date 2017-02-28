package jp.co.zenrin.music.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by v_hoang on 2/28/2017.
 */

public class HondaSystemReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        AIRecommendReceiver.setupNotify(context);
    }
}
