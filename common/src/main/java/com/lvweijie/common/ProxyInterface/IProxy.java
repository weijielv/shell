package com.lvweijie.common.ProxyInterface;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;

/**
 * Created by weijie lv on 2019/10/12.in j1
 */
public interface IProxy {

    void onCreat(Bundle savedInstanceState);
    void onStar();
    void onResum();
    void onStp();
    void onDestory();
    void onSavedInstanceState(Bundle savedInstanceState);
    void onTouch(MotionEvent motionEvent);
    void onBackPressed();

    void attach(Activity activity);
}
