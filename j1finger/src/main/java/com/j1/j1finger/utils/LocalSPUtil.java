package com.j1.j1finger.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.text.TextUtils;

/**
 * Created by weijie lv on 2019/4/18.in j1
 */

public class LocalSPUtil {
    private SharedPreferences mSharedPreference;
    private String fingerSp = "fingerSp";
    public static final String IV_NAME = "iv_bs64";

    public LocalSPUtil(Context context) {
        mSharedPreference = context.getSharedPreferences(fingerSp, Context.MODE_PRIVATE);
    }

    public String getString(String key) {
        return mSharedPreference.getString(key, null);
    }

    public boolean putString(String key, @Nullable String defValue) {
        SharedPreferences.Editor editor = mSharedPreference.edit();
        editor.putString(key, defValue);
        return editor.commit();
    }

    public boolean containsKey(String key) {
        return !TextUtils.isEmpty(getString(key));
    }
}
