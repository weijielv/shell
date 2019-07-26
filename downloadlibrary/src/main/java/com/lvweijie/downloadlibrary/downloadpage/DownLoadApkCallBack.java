package com.lvweijie.downloadlibrary.downloadpage;

/**
 * Created by weijie lv on 2016/1/14.in j1
 */
public interface DownLoadApkCallBack {

    public void onLoadSuccess(String filePath);

    public void onLoading(float value);

    public void onLoadFail();
}
