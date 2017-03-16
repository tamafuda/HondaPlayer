package jp.co.honda.music.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;

import java.util.Timer;
import java.util.TimerTask;

import jp.co.honda.music.player.AIMixAudio;
import jp.co.honda.music.player.HomeBaseFragment;
import jp.co.honda.music.player.R;
import jp.co.honda.music.player.RadarMusicActivity;
import jp.co.honda.music.service.DialogNotifyService;
import jp.co.honda.music.service.MediaPlayerService;
import jp.co.honda.music.util.SystemUtils;
import jp.co.honda.music.zdccore.HondaSharePreference;

/**
 * @Author: Hoang Vu
 * @Date: 2017/03/05
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
                boolean detectPopupWd = ((HomeBaseFragment)activity).detectPopupWindow();
                ((HomeBaseFragment) activity).stopUpdateSeekbar();
                if(detectPopupWd) {
                    activity.stopService(new Intent(activity, MediaPlayerService.class));
                }
                Intent iPlay = new Intent(activity.getBaseContext(), RadarMusicActivity.class);
                //iPlay.putExtra(HondaConstants.INTENT_NOTIFY_TO_MUSICPLAY_SRC,true);
                activity.startActivity(iPlay);
                HondaSharePreference storage = new HondaSharePreference(mContext);
                storage.storeTransitionNotifyToPlay(true);
                activity.finish();
                dialog.dismiss();
            }
        });

        FrameLayout cancel = (FrameLayout) dialog.findViewById(R.id.id_fr_dialog);
        cancel.setVisibility(View.VISIBLE);
        //cancel.setBackgroundColor(Color.TRANSPARENT);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((HomeBaseFragment)activity).stopService(new Intent(activity, DialogNotifyService.class));
                ((HomeBaseFragment)activity).startService(new Intent(activity, DialogNotifyService.class));
                dialog.dismiss();
            }
        });
        int width = (int)(mContext.getResources().getDisplayMetrics().widthPixels*1.0);
        int height = (int)(mContext.getResources().getDisplayMetrics().heightPixels*1.0);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setLayout(width,height);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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
                t.cancel();
            }
        }, 2000); // after 2 second (or 2000 miliseconds), the task will be active.
    }

    public void downloadDialog(int mess) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(mess);
        builder.setCancelable(true);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(mContext instanceof RadarMusicActivity) {
                    /*PlayerUtils.addOneMediaArrange(mContext,"COME BACK TO ME DL済");
                    Intent iPlay = new Intent(mContext, AIMixAudio.class);
                    mContext.startActivity(iPlay);
                    ((RadarMusicActivity)mContext).finish();*/
                    dialog.dismiss();
                }else if(mContext instanceof AIMixAudio){
                    dialog.dismiss();
                    Intent iPlay = new Intent(mContext, RadarMusicActivity.class);
                    mContext.startActivity(iPlay);
                    ((AIMixAudio)mContext).finish();
                }

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


}
