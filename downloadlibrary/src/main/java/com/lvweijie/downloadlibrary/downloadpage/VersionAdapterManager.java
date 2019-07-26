package com.lvweijie.downloadlibrary.downloadpage;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.StyleRes;
import android.support.v4.content.FileProvider;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.TextView;

import java.io.File;

import static android.text.Html.FROM_HTML_MODE_LEGACY;

/**
 * Created by wenjing.liu on 17/4/17 in J1.
 *
 * @author wenjing.liu
 */

public class VersionAdapterManager {

    public static int getColor(Context context, int color) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            return context.getResources().getColor(color);
        } else {
            return context.getResources().getColor(color, null);
        }
    }

    public static Drawable getDrawable(Context context, int resId) {
        Drawable drawable;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable = context.getResources().getDrawable(resId, null);
        } else {
//            try {
//                BitmapFactory.Options opt = new BitmapFactory.Options();
//                opt.inPurgeable = true;
//                opt.inInputShareable = true;
//                InputStream is = context.getResources().openRawResource(resId);
//                drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeStream(is, null, opt));
//            } catch (OutOfMemoryError e) {
//                drawable = null;
//            }
            drawable = context.getResources().getDrawable(resId);
        }
        return drawable;
    }

    public static void setTextAppearance(TextView textView, Context context, @StyleRes int resId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textView.setTextAppearance(resId);
            return;
        }
        textView.setTextAppearance(context, resId);
    }

    public static void setBackground(View view, Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(drawable);
        } else {
            view.setBackgroundDrawable(drawable);
        }
    }

    public static Uri getUri(Context context, String filePath) {
        return getUri(context, new File(filePath));
    }

    public static Uri getUri(Context context, File file) {
        Uri uri = null;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            uri = Uri.fromFile(file);
        } else {
            /**
             * Android 7.0调用系统相机拍照不再允许使用uri方式，应该替换为FileProvider
             * 并且这样可以解决MIUI系统上拍照返回size为0的情况
             */
            uri = FileProvider.getUriForFile(context, "com.lvweijie.shell.fileprovider", file);
        }

        return uri;
    }

    public static Spanned fromHtml(String source) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(source, FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(source);
        }
    }
}
