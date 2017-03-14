package jp.co.honda.music.dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import jp.co.honda.music.player.R;

import static jp.co.honda.music.common.HondaConstants.WAIT_LENGTH;

/**
 * Created by v_hoang on 3/2/2017.
 */

public class ProgressDialogTask extends AsyncTask<Integer, Void, Void> {


    private ProgressDialog dialog;
    private PopupUtils popupUtils;
    private boolean isDownload;
    private int mResourceId;
    private Context mContext;

    /**
     * isFlag is used to detected Download Popup or Share popup
     * @param context
     * @param isFlag
     */
    public ProgressDialogTask(Context context, boolean isFlag, int resourceId) {
        mContext = context;
        dialog = new ProgressDialog(context);
        popupUtils = new PopupUtils(context);
        isDownload = isFlag;
        mResourceId = resourceId;
    }
    @Override
    protected void onPreExecute() {
        dialog.setMessage(mContext.getResources().getString(mResourceId));
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
            if(isDownload) {
                popupUtils.downloadDialog(R.string.popup_download);
            }else {
                popupUtils.downloadDialog(R.string.popup_shared);
            }

        }
    }
}
