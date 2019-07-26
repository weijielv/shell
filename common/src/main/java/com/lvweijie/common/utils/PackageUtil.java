package com.lvweijie.common.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weijie lv on 2019/7/26.in j1
 */
public class PackageUtil {
    public static boolean isInstalled(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();//获取packagemanager
        List<PackageInfo> pInfo = packageManager.getInstalledPackages(0);//获取所有已安装程序的包信息
        List<String> pName = new ArrayList<>();//用于存储所有已安装程序的包名
        //从pinfo中将包名字逐一取出，压入pName list中
        if (pInfo != null) {
            for (int i = 0; i < pInfo.size(); i++) {
                String pn = pInfo.get(i).packageName;
                pName.add(pn);
            }
        }
        return pName.contains(packageName);//判断pName中是否有目标程序的包名，有TRUE，没有FALSE
    }
    /**
     *打开或者去下载应用
     * @param context 活动对应上下文对象
     * @param packageName 需要打开的应用包名
     */
    public static void openApp(Activity context, String packageName) {
           /* Intent i = new Intent();
            ComponentName cn = new ComponentName(packageName, String.valueOf(context.getClass()));
            i.setComponent(cn);
            context.startActivity(i);*/
        PackageManager manager = context.getPackageManager();
        Intent launchIntentForPackage = manager.getLaunchIntentForPackage(packageName);
        context.startActivity(launchIntentForPackage);
    }

}
