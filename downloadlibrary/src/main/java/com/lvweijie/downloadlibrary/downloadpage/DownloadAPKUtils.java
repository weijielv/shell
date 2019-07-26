package com.lvweijie.downloadlibrary.downloadpage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import com.j1.permission.handler.IPermissionResultGrantedListener;
import com.j1.permission.handler.J1Permission;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 版本更新
 *
 * @author lz
 * @version 2014年11月12日 下午5:20:15
 */
public class DownloadAPKUtils {
    public static final int DOWN_ERROR = 0;

    private static final int DOWNLAODSUCCESS = 10;
    private static final int DOWNLAODERROR = 11;
    private static final int DOWNLAODING = 12;
    private static final int DOWNLAODCANCEL = 13;
    private static final int DOWNLAODPAUSE = 12;
    private static final Object APP_UPDATE_FOLDER = "com.lvweijie.shell" ;


    private boolean downloadPause = false;
    private boolean downloadTaskCancel = false;

    private List<DownLoadApkCallBack> downLoadApkCallBacks = new ArrayList<>();

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (downLoadApkCallBacks.isEmpty()) {
                return;
            }

            switch (msg.what) {
                case DOWNLAODSUCCESS:
                    Object obj = msg.obj;
                    String path = (String) obj;
                    for (int i = 0; i < downLoadApkCallBacks.size(); i++) {
                        downLoadApkCallBacks.get(i).onLoadSuccess(path);
                    }
                    removeAllListener();
                    break;
                case DOWNLAODERROR:
                    for (int i = 0; i < downLoadApkCallBacks.size(); i++) {
                        downLoadApkCallBacks.get(i).onLoadFail();
                    }
                    removeAllListener();
                    break;
                case DOWNLAODING:
                    for (int i = 0; i < downLoadApkCallBacks.size(); i++) {
                        downLoadApkCallBacks.get(i).onLoading(msg.arg1);
                    }
                    break;
                case DOWNLAODCANCEL:
                    for (int i = 0; i < downLoadApkCallBacks.size(); i++) {
                        downLoadApkCallBacks.get(i).onLoadFail();
                    }
                    break;
            }
        }
    };


    private static DownloadAPKUtils downloadAPK;

    private DownloadAPKUtils() {
    }

    public static DownloadAPKUtils getInstance() {
        if (downloadAPK == null) {
            downloadAPK = new DownloadAPKUtils();
        }
        return downloadAPK;
    }

    public void registerDownloadListener(DownLoadApkCallBack callBack) {
        downLoadApkCallBacks.add(callBack);
    }

    public void unRegisterDownLoadListener(DownLoadApkCallBack callBack) {
        downLoadApkCallBacks.remove(callBack);
    }

    private void removeAllListener() {
        downLoadApkCallBacks.clear();
    }


    public void setDownloadTaskCancel() {
        downloadTaskCancel = false;
    }

    public String getApkPath(String pakName) {
        return Environment.getExternalStorageDirectory().getAbsoluteFile() + "/"
                + APP_UPDATE_FOLDER + "/" + pakName;
    }

    /**
     * 文件夹的安装包是否最新的安装包
     *
     * @param context
     * @param apkName
     * @return
     */
    public boolean readApkInfoIsLaster(Context context, String apkName) {
        String archiveFilePath = getApkPath(apkName);//安装包路径
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(archiveFilePath, PackageManager.GET_ACTIVITIES);
        return false;
    }

   /* //是否需要在通知栏中显示进度。
    public void downLoadApk(final Activity activity, final String path, final String apkName, boolean showNotify){
        if (showNotify){
            NotificationUtils notifyUtils = new NotificationUtils();
            notifyUtils.senderNotify(activity);
        }
        downLoadApk(activity,path,apkName);
    }*/

    /*
     * 从服务器中下载APK
     */
    public void downLoadApk(final Activity activity, final String path, final String apkName) {
        J1Permission.create(activity).permissionWriteStorage().request(new IPermissionResultGrantedListener() {
            @Override
            public void onPermissionGranted(int requestCode) {
                new Thread() {
                    @Override
                    public void run() {
                        if (isInstallNewest(apkName, activity)) {
                            return;
                        }
                        if (readApkInfoIsLaster(activity, apkName)) {
                            Message msg = new Message();
                            msg.obj = getApkPath(apkName);
                            msg.what = DOWNLAODSUCCESS;
                            handler.sendMessage(msg);
                            return;
                        }
                        getFileFromServer(path, apkName);
                    }
                }.start();
            }
        });
    }

    /**
     * 判断安装的应用是否最新版本
     *
     * @param apkName
     * @param context
     * @return
     */
    private boolean isInstallNewest(String apkName, Context context) {
        /*String archiveFilePath = getApkPath(apkName);//安装包路径
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(archiveFilePath, PackageManager.GET_ACTIVITIES);
        if (info != null) {
            return true;
        }*/
        return false;
    }

    private void getFileFromServer(String path, String apkName) {
        // 如果相等的话表示当前的sdcard挂载在手机上并且是可用的
//        LogUtils.d("getFileFromServer path :" + path);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            try {

                URL url = new URL(path);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                // 获取到文件的大小
                int totalLength = conn.getContentLength();
                InputStream is = conn.getInputStream();
                File folder = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/"
                        + APP_UPDATE_FOLDER);
                if (!folder.exists()) {
                    folder.mkdirs();
                }
                //只创建了一个file对象，并没有在磁盘上创建file。。所以通过file.exists()可以判断是否存在这个文件
                File file = new File(folder, apkName);

                //优化下载，防止存在缓存文件导致的下载失败
                if(file.exists()){
                    //删除
                    if (file.delete()){
                        //删除成功
                        file = new File(folder,apkName);
                    }
                }

                FileOutputStream fos = new FileOutputStream(file);
                BufferedInputStream bis = new BufferedInputStream(is);
                byte[] buffer = new byte[1024];
                int len;
                int total = 0;
                long time = System.currentTimeMillis();
                while ((len = bis.read(buffer)) != -1 && !downloadTaskCancel) {
                    fos.write(buffer, 0, len);
                    total += len;
                    //每500毫秒通知更新一次ui
                    if (System.currentTimeMillis() - time >= 500) {
                        time = System.currentTimeMillis();
                        // 获取当前下载量
                        Message msg = new Message();
                        msg.arg1 = (int) ((float) total / totalLength * 100);
                        msg.what = DOWNLAODING;
                        handler.sendMessage(msg);
                    }

                }
                if (downloadTaskCancel) {
                    handler.sendEmptyMessage(DOWNLAODCANCEL);
                    return;
                }
                Message msg = new Message();
                msg.obj = file.getAbsolutePath();
                msg.what = DOWNLAODSUCCESS;
                handler.sendMessage(msg);
                fos.close();
                bis.close();
                is.close();
                return;

            } catch (Exception e) {
                e.printStackTrace();
                handler.sendEmptyMessage(DOWNLAODERROR);
                return;
            }
        } else {
            handler.sendEmptyMessage(DOWNLAODERROR);
            return;
        }
    }

    // 安装apk
    public void installApk(Context mContext, File file) {
        //SharedPreferencesUtils.getInstance(mContext).putBoolean("isUpdateSelf", true);
        Intent intent = new Intent();
        // 执行动作
        intent.setAction(Intent.ACTION_VIEW);
        // 执行的数据类型
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        mContext.startActivity(intent);
    }

    // 安装apk
    public void installApk(Context mContext, String apkPath) {
        if (apkPath == null) {
            return;
        }
        try {
            Intent intent = new Intent();
            // 执行动作
            intent.setAction(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            // 执行的数据类型
            Uri uri = VersionAdapterManager.getUri(mContext, apkPath);
            //intent.setDataAndType(Uri.parse(apkPath), "application/vnd.android.package-archive");
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            mContext.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 判断该应用在手机中的是否需要安装
     *
     * @param pm          PackageManager
     * @param packageName 要判断应用的包名
     * @param versionCode 要判断应用的版本号
     */
    private boolean isNotNeedInstall(PackageManager pm, String packageName, int versionCode) {
        List<PackageInfo> pakageinfos = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
        for (PackageInfo pi : pakageinfos) {
            String pi_packageName = pi.packageName;
            int pi_versionCode = pi.versionCode;
            //如果这个包名在系统已经安装过的应用中存在
            if (packageName.endsWith(pi_packageName)) {
                if (versionCode == pi_versionCode) {
//                    LogUtils.e("===test" + "已经安装，不用更新，可以卸载该应用");
                    return true;
                } else if (versionCode > pi_versionCode) {
//                    LogUtils.e("====test" + "已经安装，有更新");
                    return false;
                }
            }
        }
//        LogUtils.e("===test" + "未安装该应用，可以安装");
        return false;
    }

}
