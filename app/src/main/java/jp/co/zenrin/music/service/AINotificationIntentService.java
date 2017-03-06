package jp.co.zenrin.music.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import jp.co.zenrin.music.player.MusicPlayActivity;
import jp.co.zenrin.music.player.R;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class AINotificationIntentService extends IntentService {

    private static final int NOTIFICATION_ID = 101;
    private static final String ACTION_START = "ACTION_START";
    private static final String ACTION_DELETE = "ACTION_DELETE";

    /**
     * Construtor method
     */
    public AINotificationIntentService() {
        super(AINotificationIntentService.class.getSimpleName());
    }

    /**
     * Create intent to start notification service
     * @param context
     * @return
     */
    public static Intent createIntentStartNotificationService(Context context) {
        Intent intent = new Intent(context, AINotificationIntentService.class);
        intent.setAction(ACTION_START);
        return intent;
    }

    /**
     * * Create intent to delete notification service
     * @param context
     * @return
     */
    public static Intent createIntentDeleteNotification(Context context) {
        Intent intent = new Intent(context, AINotificationIntentService.class);
        intent.setAction(ACTION_DELETE);
        return intent;
    }

    /**
     * Process starting notification
     */
    private void processStartNotification() {

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setContentTitle("運手がお疲れみたい！")
                .setAutoCancel(true)
                .setSound(uri)
                .setSmallIcon(R.drawable.icon_honda)
                .setContentText("音楽アレンジでお盛り上がろう!");

        Intent mainIntent = new Intent(this, MusicPlayActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                NOTIFICATION_ID,
                mainIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        //builder.setDeleteIntent(AIRecommendReceiver.getDeleteIntent(this));

        final NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, builder.build());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(getClass().getSimpleName(), "onHandleIntent, started handling a notification event");
        try {
            String action = intent.getAction();
            if (ACTION_START.equals(action)) {
                processStartNotification();
                /*AIRecommendReceiver.cancelNotify(getBaseContext());
                Intent iController = new Intent(getBaseContext(), HomeBaseFragment.class);
                iController.putExtra(HondaConstants.BROADCAST_AI_RECOMMEND, true);
                iController.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(iController);*/
            }
        } finally {
            WakefulBroadcastReceiver.completeWakefulIntent(intent);
        }
    }
}
