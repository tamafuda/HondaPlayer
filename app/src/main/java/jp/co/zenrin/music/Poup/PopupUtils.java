package jp.co.zenrin.music.Poup;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;

import java.util.Timer;
import java.util.TimerTask;

import jp.co.zenrin.music.player.R;

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
    public void notifyDialogCustom(int tltStringResId) {
        // custom dialog
        // Should be use "this"
        // getApplicationContext() or getBaseContext() is false
        final Dialog dialog = new Dialog(mContext);

        dialog.setContentView(R.layout.notify_pupup_custom);
        dialog.setCancelable(false);
        Button btn = (Button) dialog.findViewById(R.id.close_popup);
        btn.setText(tltStringResId);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.getWindow().setGravity(Gravity.TOP);
        dialog.show();
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


}
