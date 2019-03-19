package com.lvweijie.ljlogin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;

@Route(path = "/login/loginActivity")
public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViewById(R.id.login_tv_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //success
                String path = LoginActivity.this.getIntent().getStringExtra("path");
                ARouter.getInstance().build(path)
                        .navigation();
            }
        });
    }
}
