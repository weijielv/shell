package com.lvweijie.shell;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.alibaba.android.arouter.launcher.ARouter;
import com.lvweijie.ljlogin.LoginActivity;

public class MainActivity extends AppCompatActivity {

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

    }
}
