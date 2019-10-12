package com.lvweijie.common.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.Log;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

/**
 * Created by weijie lv on 2019/10/12.in j1
 */
public class PluginManager {
    private static final PluginManager ourInstance = new PluginManager();

    private DexClassLoader dexClassLoader;
    private Resources resources;
    private Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    public String getEntryName() {
        return entryName;
    }

    private String entryName;


    public static PluginManager getInstance() {
        return ourInstance;
    }

    public void loadPath(String path){
        try {
            PackageInfo packageInfo  = context.getPackageManager().getPackageArchiveInfo(path,
                    PackageManager.GET_ACTIVITIES);
            entryName = packageInfo.activities[0].name;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(this.getClass().getSimpleName(),"apk 路径出错,\n"+" path = "+path);
        }

        File dexOutDir = context.getDir("dex",Context.MODE_PRIVATE);
        dexClassLoader = new DexClassLoader(path,dexOutDir.getAbsolutePath(),null,context.getClassLoader());

        AssetManager assets = null;
        try {
            assets = AssetManager.class.newInstance();
            Method method = AssetManager.class.getMethod("addAssetPath",String.class);
            method.invoke(assets,path);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        resources = new Resources(assets,context.getResources().getDisplayMetrics(),context.getResources().getConfiguration());

    }

    private PluginManager() {
    }

    public DexClassLoader getDexClassLoader() {
        return dexClassLoader;
    }

    public void setDexClassLoader(DexClassLoader dexClassLoader) {
        this.dexClassLoader = dexClassLoader;
    }

    public Resources getResources() {
        return resources;
    }

    public void setResources(Resources resources) {
        this.resources = resources;
    }
}
