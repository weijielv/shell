package com.j1.permission.log;

import com.j1.permission.BuildConfig;

/**
 * Created by wenjing.liu on 19/6/21 in J1.
 * <p>
 * 日志
 *
 * @author wenjing.liu
 */
public class Log {
    private static final String TAG = "permission";

    public static void d(String msg) {
        if (!BuildConfig.DEBUG_PERMISSION) {
            return;
        }
        android.util.Log.d(TAG, msg);
    }

    public static void w(String msg) {
        if (!BuildConfig.DEBUG_PERMISSION) {
            return;
        }
        android.util.Log.w(TAG, msg);
    }
}
