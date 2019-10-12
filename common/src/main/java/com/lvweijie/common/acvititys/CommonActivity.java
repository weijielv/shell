package com.lvweijie.common.acvititys;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.lvweijie.common.R;
import com.lvweijie.common.utils.PluginManager;

import java.io.File;

public class CommonActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);
    }

    public void onClick(View view){
        PluginManager.getInstance().setContext(this);
        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator + "dex.apk";
        PluginManager.getInstance().loadPath(path);

        Intent intent = new Intent(this,ProxyAcitivity.class);
        intent.putExtra("className",PluginManager.getInstance().getEntryName());
        startActivity(intent);
    }
}

