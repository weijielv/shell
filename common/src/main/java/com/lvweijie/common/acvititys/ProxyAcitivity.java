package com.lvweijie.common.acvititys;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;

import com.lvweijie.common.ProxyInterface.IProxy;
import com.lvweijie.common.utils.PluginManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by weijie lv on 2019/10/12.in j1
 */
public class ProxyAcitivity extends Activity {


    public IProxy proxy ;
    String className = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        className = getIntent().getStringExtra("className");
        try {
            Class clazz = getClassLoader().loadClass(className);
            Constructor<IProxy> constructor =clazz.getConstructor(new Class[]{});
            proxy = constructor.newInstance(new Object[]{});
            Bundle bundle = new Bundle();
            proxy.attach(this);
            proxy.onCreat(bundle);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        proxy.onStar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        proxy.onResum();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        proxy.onDestory();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        proxy.onBackPressed();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        proxy.onTouch(event);
        return super.onTouchEvent(event);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        proxy.onSavedInstanceState(outState);
    }

    @Override
    protected void onStop() {
        super.onStop();
        proxy.onStp();
    }

    @Override
    public ClassLoader getClassLoader() {
        return  PluginManager.getInstance().getDexClassLoader();
    }
}

