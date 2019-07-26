package com.j1.permission.handler;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.j1.permission.R;

/**
 * Created by wenjing.liu on 19/4/18 in J1.
 * <p>
 * 显示一个dialog的UI
 *
 * @author wenjing.liu
 */
public class PermissionDialogHandler {

    /***
     * 显示一个dialog
     * @param context
     * @param title
     * @param message
     * @param positive
     * @param clickListener
     */
    protected static void showMessageDialog(Context context, String title, String message, int positive,
                                            DialogInterface.OnClickListener clickListener) {
        PermissionDialog.Builder builder = new PermissionDialog.Builder(context);
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }
        builder.setMessage(message)
                .setPositiveButton(positive, clickListener)
                .setNegativeButton(android.R.string.cancel, null)
                .create()
                .show();
    }

    /***
     * 显示拨打电话的对话框
     * @param context
     * @param phoneRes 需要拨打的手机号码,显示在对话框的电话号码的格式，支持中间加-，如"4007-800-800"，也可以"13795460038"
     */
    protected static void callPhoneDialog(final Context context, final String phoneRes) {

        showMessageDialog(context, phoneRes, null, R.string.perm_call, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callPhoneActivity(context, phoneRes);
                dialog.dismiss();
            }
        });
    }

    /**
     * 拨打电话
     *
     * @param context
     * @param uriString
     */
    private static void callPhoneActivity(Context context, String uriString) {

        if (TextUtils.isEmpty(uriString)) {
            return;
        }
        if (uriString.contains("-")) {
            uriString = uriString.replace("-", "");
        }
        if (!uriString.startsWith("tel:")) {
            uriString = String.format("tel:%s", uriString);
        }
        try {
            Intent telIntent = new Intent(Intent.ACTION_CALL, Uri.parse(uriString));
            context.startActivity(telIntent);
        } catch (SecurityException e) {
        }

    }

}
