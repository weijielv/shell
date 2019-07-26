package com.j1.permission.handler;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

/**
 * Created by wenjing.liu on 19/6/20 in J1.
 * 根据不同的机型跳转到对应的设置权限的页面
 *
 * @author wenjing.liu
 */
public class PermissionSettingActivity {

    private static final String MANU = Build.MANUFACTURER.toLowerCase();

    public static void startSettingActivity(Context context) {
        Intent intent = null;
        if (MANU.contains("huawei")) {
            intent = huawei(context);
        } else if (MANU.contains("xiaomi")) {
            intent = xiaomi(context);
        } else if (MANU.contains("oppo")) {
            intent = oppo(context);
        } else if (MANU.contains("vivo")) {
            intent = vivo(context);
        } else if (MANU.contains("meizu")) {
            intent = meizu(context);
        }

        if (intent == null || !hasIntent(context, intent)) {
            intent = google(context);
        }

        try {
            context.startActivity(intent);
        } catch (Exception ignored) {
            intent = google(context);
            context.startActivity(intent);
        }
    }

    /**
     * 原生系统
     *
     * @param context
     * @return
     */
    private static Intent google(Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        return intent;
    }

    /**
     * 华为权限管理
     *
     * @param context
     * @return
     */
    private static Intent huawei(Context context) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity");
        intent.setComponent(comp);
        return intent;
    }

    /**
     * 小米
     *
     * @param context
     * @return
     */
    private static Intent xiaomi(Context context) {
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        ComponentName componentName = new ComponentName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
        intent.setComponent(componentName);
        intent.putExtra("extra_pkgname", context.getPackageName());
        return intent;
    }

    /**
     * oppo
     *
     * @param context
     * @return
     */
    private static Intent oppo(Context context) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("packageName", context.getPackageName());
        ComponentName comp = new ComponentName("com.color.safecenter", "com.color.safecenter.permission.PermissionManagerActivity");
        intent.setComponent(comp);
        return intent;
    }

    /**
     * vivo
     *
     * @param context
     * @return
     */
    private static Intent vivo(Context context) {
        Intent intent = new Intent();
        intent.putExtra("packagename", context.getPackageName());
        intent.setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.safeguard.SoftPermissionDetailActivity"));
        return intent;
    }

    /**
     * meizu
     *
     * @param context
     * @return
     */
    private static Intent meizu(Context context) {
        Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
        intent.putExtra("packageName", context.getPackageName());
        intent.setComponent(new ComponentName("com.meizu.safe", "com.meizu.safe.security.AppSecActivity"));
        return intent;
    }

    private static boolean hasIntent(Context context, Intent intent) {
        return !context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).isEmpty();
    }

}
