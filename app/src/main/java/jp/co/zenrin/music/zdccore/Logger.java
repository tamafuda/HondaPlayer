package jp.co.zenrin.music.zdccore;

import android.util.Log;

/**
 * @Author: Hoang Vu
 * @Date: 2017/02/25
 */

public class Logger {
    private static final int MSG_MAX_LENGTH = 4096;
    // Need to config after
    private final boolean isLog;
    private final String mTag;

    //	public static void init(Context context)
    //	{
    //		sIsLog = context.getResources().getBoolean(R.bool.logging);
    //	}

    public Logger(String Tag, boolean ismLog) {
        this.isLog = ismLog;
        this.mTag = Tag;
    }

    // VERBOSE

    public void v(String TAG, String msg) {
        if (isLog) {
            Log.v(TAG, msg);
        }
    }

    public void v(String msg) {
        v(mTag, msg);
    }

    // DEBUG

    public void d(String TAG, String msg) {
        if (isLog) {
            Log.d(TAG, msg);
        }
    }
    public void d(String msg)
    {
        d(mTag, msg);
    }

    // INFO

    public void i(String TAG, String msg) {
        if (isLog) {
            while (msg.length() > MSG_MAX_LENGTH) {
                Log.i(TAG, msg.substring(0, MSG_MAX_LENGTH));
                msg = msg.substring(MSG_MAX_LENGTH);
            }

            Log.i(TAG, msg);
        }
    }

    public void i(String msg) {

        i(mTag, msg);
    }

    // ERROR

    public void e(String TAG, String msg)
    {
        Log.e(TAG, msg);
    }

    public void e(String msg)
    {
        e(mTag, msg);
    }

    // WARNING

    public void w(String tag, String msg)
    {
        Log.w(tag, msg);
    }

    public void w(String msg)
    {
        w(mTag, msg);
    }


    // Exception
    public void e(Exception e)
    {
        e(Log.getStackTraceString(e));
    }

    public void e(Exception e, boolean sendLogs)
    {
        e(Log.getStackTraceString(e));
    }

    public void e(OutOfMemoryError e, boolean sendLogs)
    {
        e(Log.getStackTraceString(e));
    }

    public void e(String tag, String msg, Exception e)
    {
        Log.e(tag, msg, e);
    }

    public void e(String msg, Exception e)
    {
        e(mTag, msg, e);
    }

    public void printError(String msg, Exception e)
    {
        Log.e(mTag, msg, e);
    }

    public void printError(String msg)
    {
        Log.e(mTag, msg);
    }

}
