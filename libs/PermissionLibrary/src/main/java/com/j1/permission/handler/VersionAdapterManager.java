package com.j1.permission.handler;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import java.io.File;

/**
 * Created by wenjing.liu on 17/4/17 in J1.
 * <p>
 * 版本适配
 *
 * @author wenjing.liu
 */

public class VersionAdapterManager {

    public static Uri getUri(Context context, File file) {
        Uri uri = null;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            uri = Uri.fromFile(file);
        } else {
            /**
             * Android 7.0调用系统相机拍照不再允许使用uri方式，应该替换为FileProvider
             * 并且这样可以解决MIUI系统上拍照返回size为0的情况
             */
            uri = FileProvider.getUriForFile(context, getFileProviderName(context), file);
        }

        return uri;
    }

    private static String getFileProviderName(Context context) {
        return context.getExternalFilesDir("provider").getAbsolutePath();
    }
}
