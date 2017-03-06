package jp.co.zenrin.music.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import java.util.Timer;
import java.util.TimerTask;

import jp.co.zenrin.music.player.MusicPlayActivity;
import jp.co.zenrin.music.player.R;
import jp.co.zenrin.music.util.SystemUtils;
import jp.co.zenrin.music.zdccore.HondaSharePreference;

/**
 * Created by v_hoang on 3/2/2017.
 */

public class PopupUtils{

    Context mContext;

    public PopupUtils(Context context){
        this.mContext = context;
    }

    /**
     *
     * @param tltStringResId
     */
    public Dialog notifyDialogCustom(final Activity activity, int tltStringResId) {
        // custom dialog
        // Should be use "this"
        // getApplicationContext() or getBaseContext() is false
        final Dialog dialog = new Dialog(mContext);
        String notifyTime = SystemUtils.getSystemTimeNotify();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.notify_pupup_custom);
        dialog.setCancelable(false);
        //dialog.setTitle("運手がお疲れみたい！   " + notifyTime);
        Button btn = (Button) dialog.findViewById(R.id.close_popup);
        String message = "";
        String str1 = activity.getResources().getString(R.string.popup_notify_title);
        String str2 = activity.getResources().getString(R.string.popup_notify_content);
        message = str1 + " " + notifyTime + "\n" + str2;
        btn.setText(message);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iPlay = new Intent(activity.getBaseContext(), MusicPlayActivity.class);
                //iPlay.putExtra(HondaConstants.INTENT_NOTIFY_TO_MUSICPLAY_SRC,true);
                activity.startActivity(iPlay);
                HondaSharePreference storage = new HondaSharePreference(mContext);
                storage.storeTransitionNotifyToPlay(true);
                activity.finish();
                dialog.dismiss();
            }
        });
        dialog.getWindow().setGravity(Gravity.TOP);
        //dialog.show();
        return dialog;
    }

    public void autoCloseDialog(int tltStringResId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(tltStringResId);
        builder.setCancelable(true);
        final AlertDialog dlg = builder.create();

        dlg.show();

        final Timer t = new Timer();
        t.schedule(new TimerTask() {
            public void run() {
                dlg.dismiss(); // when the task active then close the dialog
                t.cancel(); // also just top the timer thread, otherwise, you may receive a crash report
            }
        }, 2000); // after 2 second (or 2000 miliseconds), the task will be active.
    }

    public void downloadDialog(int mess) {
        AlertDialog.Builder builder = new AlertDialog.Builder((mContext));
        builder.setMessage(mess);
        builder.setCancelable(true);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create();
        builder.show();
    }


}
