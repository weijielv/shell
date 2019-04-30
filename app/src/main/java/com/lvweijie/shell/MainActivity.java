package com.lvweijie.shell;

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
import com.lvweijie.ljlogin.LoginActivity;

import static android.security.keystore.KeyProperties.PURPOSE_DECRYPT;
import static android.security.keystore.KeyProperties.PURPOSE_ENCRYPT;

public class MainActivity extends AppCompatActivity {
    private static final String SPLoginkey = "j1sploginkey";
    private static final String SPPaykey = "j1sppaykey";
    private static final String TAG = "AppCompatActivity";
    private EditText etLogin;
    private Button btlogin;

    @SuppressLint("CutPasteId")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.shell_btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build("/pay/payActivity")
                        .withLong("key1", 666L)
                        .withString("key2", "888")
                        .navigation();
                //MainActivity.this.startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });
        etLogin = findViewById(R.id.et_login);
        btlogin = findViewById(R.id.bt_login);
        final FingerManager fingerManager = FingerManager.getInstance(MainActivity.this);
        if (fingerManager.isOpenFingerLogin(SPLoginkey) == PURPOSE_DECRYPT){
            etLogin.setVisibility(View.GONE);
            btlogin.setText("指纹登陆");
        }
        findViewById(R.id.bt_login).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                FingerPrintStatus type = FingerManager.getFingerprintAvailable(MainActivity.this);
                if (FingerPrintStatus.HAS_FINGER == type) {

                    //showDialog();
                    if (fingerManager.isOpenFingerLogin(SPLoginkey) == PURPOSE_ENCRYPT) {
                        String pwd = etLogin.getText().toString();
                        if (TextUtils.isEmpty(pwd)){
                            Toast.makeText(MainActivity.this,"请输入密码",Toast.LENGTH_LONG).show();
                            return;
                        }
                        fingerManager.applyAuthenticate(SPLoginkey, pwd, "title", "描述", callBack);
                    } else {

                        fingerManager.authenticate(SPLoginkey, "title", "miaosu", callBack);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "不支持", Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    FingerAuthenticationCallBack callBack = new FingerAuthenticationCallBack() {
        @Override
        public void onAuthenticationFail(String result) {

        }

        @Override
        public void onAuthenticationSucceeded(String result) {
            Log.e(TAG,result);
            Toast.makeText(MainActivity.this,result,Toast.LENGTH_LONG).show();
        }

        @Override
        public void onAuthenticationError(int errorCode, CharSequence errString) {

        }

        @Override
        public void onAuthenticationHelp(int helpCode, CharSequence helpString) {

        }

        @Override
        public void onCancel() {
            Log.e(TAG,"user cancel");
        }
    };
}

