package com.lvweijie.dex;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.lvweijie.common.ProxyInterface.IProxy;

/**
 * Created by weijie lv on 2019/10/12.in j1
 */
public class BaseAcitivity extends AppCompatActivity implements IProxy {
    Activity that ;


    @Override
    public ClassLoader getClassLoader() {
        if (that != null){
            return that.getClassLoader();
        }
        return super.getClassLoader();
    }

    @Override
    public void setContentView(View view) {
        if (that != null){
            that.setContentView(view);
            return;
        }
        super.setContentView(view);
    }

    @Override
    public void setContentView(int layoutResID) {
        if (that != null){
            that.setContentView(layoutResID);
            return;
        }
        super.setContentView(layoutResID);
    }

    @Override
    public <T extends View> T findViewById(int id) {
        if (that != null){
            return that.findViewById(id);
        }
        return super.findViewById(id);
    }

    @NonNull
    @Override
    public LayoutInflater getLayoutInflater() {
        if (that != null){
            return that.getLayoutInflater();
        }
        return super.getLayoutInflater();
    }

    @Override
    public Window getWindow() {
        if (that != null){
            return that.getWindow();
        }
        return super.getWindow();
    }

    @Override
    public WindowManager getWindowManager() {
        if (that != null){
            return that.getWindowManager();
        }
        return super.getWindowManager();
    }


    @Override
    public void onCreat(Bundle savedInstanceState) {
        onCreate(savedInstanceState);
    }

    @Override
    public void onStar() {
        onStar();
    }

    @Override
    public void onResum() {
        onResume();
    }

    @Override
    public void onStp() {
        onStop();
    }

    @Override
    public void onDestory() {

    }

    @Override
    public void onSavedInstanceState(Bundle savedInstanceState) {

    }

    @Override
    public void onTouch(MotionEvent motionEvent) {

    }

    @Override
    public void attach(Activity activity) {
        that= activity;
    }
}
