package jp.co.zenrin.music.dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import jp.co.zenrin.music.player.R;

import static jp.co.zenrin.music.common.HondaConstants.WAIT_LENGTH;

/**
 * Created by v_hoang on 3/2/2017.
 */

public class ProgressDialogTask extends AsyncTask<Integer, Void, Void> {


    private ProgressDialog dialog;
    private PopupUtils popupUtils;

    public ProgressDialogTask(Context context) {
        dialog = new ProgressDialog(context);
        popupUtils = new PopupUtils(context);
    }
    @Override
    protected void onPreExecute() {
        dialog.setMessage("シェア中。。。");
        dialog.show();
    }

    @Override
    protected Void doInBackground(final Integer... i) {
        long start = System.currentTimeMillis();
        while(!isCancelled()&&System.currentTimeMillis()-start< WAIT_LENGTH){
        }
        return null;
    }

    @Override
    protected void onPostExecute(final Void v) {
        if(dialog.isShowing()) {
            dialog.dismiss();
            popupUtils.autoCloseDialog(R.string.popup_shared);
        }
    }
}
