package com.lvweijie.downloadlibrary.downloadpage;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.j1.permission.handler.FragmentSinglePermissionManager;
import com.j1.permission.handler.IPermissionResultGrantedListener;
import com.j1.permission.handler.J1Permission;
import com.lvweijie.downloadlibrary.R;

import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Permission;
import java.text.DecimalFormat;

/**
 * Created by weijie lv on 2016/1/14.in j1
 * 版本升级下载app页面
 */
@Route(path = "/download/downloadActivity",extras = 0)
public class DownloadApkActivity extends Activity implements DownLoadApkCallBack {

    private TextView tvDownloadNow;
    private TextView tvDownloadNewVersion;
    private TextView tvDownloadMessage;
    private TextView tvDownloadSize;
    private TextView tv_progress;
    private View UI_pre;
    private View UI_later;
    private View rocket;
    private View shadow;
    private View cloud;
    private ImageView ivFlame;
    private ImageView ivClose;
    private ProgressBar progressBar;

    private String apkpath = "";
    private String apkurl = "https://ws-oss-app.syrnight.com/cpb/cpbangzy.apk";
    private long lastPressTime;
    private AsyncTask<String, Integer, Integer> task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        /*getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        setContentView(R.layout.dialog_download_apk);
        initWidgets();
    }

    public void initWidgets() {
        tvDownloadNow = (TextView) findViewById(R.id.tv_downloadapk_update_now);
        tvDownloadNewVersion = (TextView) findViewById(R.id.tv_downloadapk_new_version);
        tvDownloadMessage = (TextView) findViewById(R.id.tv_downloadapk_new_dec);
        tvDownloadSize = (TextView) findViewById(R.id.tv_downloadapk_new_size);
        tv_progress = (TextView) findViewById(R.id.tv_download_apk_progress_provalue);
        UI_pre = findViewById(R.id.download_apk_view_pre);
        UI_later = findViewById(R.id.download_apk_view_later);
        rocket = findViewById(R.id.rl_downloadapk_rocket);
        shadow = findViewById(R.id.iv_download_apk_shadow);
        cloud = findViewById(R.id.iv_download_apk_cloud);
        ivFlame = findViewById(R.id.iv_download_apk_flame);
        ivClose =  findViewById(R.id.iv_download_apk_close);
        progressBar = (ProgressBar) findViewById(R.id.pb_download);

        tvDownloadNow.setOnClickListener(clickListener);
        ivClose.setOnClickListener(clickListener);
    }

    /**
     * View点击监听
     */
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int i = v.getId();
            if (i == R.id.tv_downloadapk_update_now) {
                downLoadApk();
                //finish();
            } else if (i == R.id.iv_download_apk_close) {
                finish();
                //取消监听
                //DownloadAPKUtils.getInstance().setDownloadTaskCancel();
            }
        }
    };

    private void downLoadApk() {
        UI_later.setVisibility(View.VISIBLE);
        UI_pre.setVisibility(View.GONE);
        DownloadAPKUtils.getInstance().registerDownloadListener(this);
        DownloadAPKUtils.getInstance().downLoadApk(DownloadApkActivity.this,
                apkurl, "cpb.apk");
    }

    private void launch() {
        TranslateAnimation animation = new TranslateAnimation(0, 0, 0, -2110);
        animation.setDuration(250);
        animation.setAnimationListener(new VisibilityAnimationListener(rocket, 1));
        rocket.startAnimation(animation);
        animation.startNow();
    }

    @Override
    public void onLoadSuccess(String filePath) {
        apkpath = filePath;
        tv_progress.setText("下载完成");
        launch();
    }

    @Override
    public void onLoading(float value) {
        progressBar.setProgress((int) value);
        tv_progress.setText("已经下载："+value+"%");
    }

    @Override
    public void onLoadFail() {

    }


    public class VisibilityAnimationListener implements Animation.AnimationListener {

        private View mVisibilityView;
        private int index = 0;

        public VisibilityAnimationListener(View view, int index) {
            mVisibilityView = view;
            this.index = index;
        }

        public void setVisibilityView(View view) {
            mVisibilityView = view;
        }

        @Override
        public void onAnimationStart(Animation animation) {
            if (mVisibilityView != null) {
            }
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if (mVisibilityView != null) {

                if (mVisibilityView.getId() == R.id.rl_downloadapk_rocket) {
                    mVisibilityView.setVisibility(View.GONE);
                    //显示蘑菇云
                    cloud.setVisibility(View.VISIBLE);
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
                        DownloadAPKUtils.getInstance().installApk(DownloadApkActivity.this, apkpath);
                    }else {
                        if (getPackageManager().canRequestPackageInstalls()){
                            DownloadAPKUtils.getInstance().installApk(DownloadApkActivity.this, apkpath);
                            return;
                        }
                        //安装
                        J1Permission.create(DownloadApkActivity.this).
                                permission("android.permission.REQUEST_INSTALL_PACKAGES").
                                request(new IPermissionResultGrantedListener() {
                                    @Override
                                    public void onPermissionGranted(int requestCode) {
                                        DownloadAPKUtils.getInstance().installApk(DownloadApkActivity.this, apkpath);
                                    }
                                });
                    }

                    //finish();
                }
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            //强制更新
            if (isForceUpdateVersion()) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastPressTime > 2000) {
                    lastPressTime = currentTime;
                } else {
                    finish();
                }
            } else {
                //非强制更新
                finish();
                //取消监听
                DownloadAPKUtils.getInstance().setDownloadTaskCancel();

            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    private boolean isForceUpdateVersion() {
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        if (task != null && task.getStatus() != AsyncTask.Status.FINISHED) {
            task.cancel(true);
            task = null;
        }
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        //overridePendingTransition(android.R.anim.fade_in,R.anim.dialog_zoom_out_anim);
    }


}
