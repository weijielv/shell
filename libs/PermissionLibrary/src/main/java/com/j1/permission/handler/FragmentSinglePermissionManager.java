package com.j1.permission.handler;

import android.app.Fragment;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;

import java.util.Arrays;

/**
 * Created by wenjing.liu on 19/6/13 in J1.
 * <p>
 * 处理单个权限的校验和验证结果
 *
 * @author wenjing.liu
 */
public class FragmentSinglePermissionManager extends FragmentPermissionHandler {
    private IPermissionResultGrantedListener grantedListener;

    protected FragmentSinglePermissionManager() {
    }

    protected void setIPermissionResultGrantedListener(IPermissionResultGrantedListener listener) {
        this.grantedListener = listener;
    }

    /***
     * 申请权限 去弹出对话框来进行权限校验 低版本直接授权在J1Permission中处理
     * @param permission 需要校验的权限
     */
    protected void requestPermission(Fragment fragment, String permission) {
        //根据权限获取对应的requestCode
        int requestCode = getPermissionRequestCode(permission);
        // 不合法的直接单个授权
        if (isIllegalSinglePermission(requestCode)) {
            onPermissionGranted(fragment, grantedListener, requestCode);
            return;
        }
        //进行判断该权限的弹出框
        int checkSelfPermission;
        try {
            checkSelfPermission = checkSelfPermission(fragment, permission);
        } catch (Exception e) {
            return;
        }
        //默认的为拒绝,允许则执行允许的回调
        if (checkSelfPermission == PackageManager.PERMISSION_GRANTED) {
            onPermissionGranted(fragment, grantedListener, requestCode);
            return;
        }
        // 如果第二次弹出权限申请的对话框，会出现“以后不再弹出”的提示框，如果用户勾选了，
        // 你再申请权限，则shouldShowRequestPermissionRationale返回true，意思是说要给用户一个 解释，告诉用户为什么要这个权限。
        if (shouldShowRequestPermissionRationale(fragment, permission)) {
            shouldShowPermissionRationale(fragment, Arrays.asList(new String[]{permission}));
            return;
        }
        //弹出系统的授权框,用户允许和拒绝之后会回调到Activity的onRequestPermissionsResult
        requestPermissions(fragment, new String[]{permission}, requestCode);
    }

    /***
     * 回调处理结果
     * TODO http://blog.csdn.net/hudashi/article/details/50775180
     * Android原生系统中，如果第二次弹出权限申请的对话框，会出现“以后不再弹出”的提示框，如果用户勾选了，你再申请权限，则shouldShowRequestPermissionRationale返回true，意思是说要给用户一个 解释，告诉用户为什么要这个权限。
     * 然而，在实际开发中，需要注意的是，很多手机对原生系统做了修改，比如小米，小米4的6.0的shouldShowRequestPermissionRationale 就一直返回false，而且在申请权限时，如果用户选择了拒绝，则不会再弹出对话框了。。。。
     * 所以说这个地方有坑，我的解决方法是，在回调里面处理，如果用户拒绝了这个权限，则打开本应用信息界面，由用户自己手动开启这个权限
     * （解决方案如下：在onRequestPermissionsResult(系统弹出框里面的允许与拒绝回调的方法)函数中进行检测，如果返回PERMISSION_DENIED，则去调用shouldShowRequestPermissionRationale函数，如果返回false代表用户已经禁止该权限（上面的3和4两种情况），弹出dialog告诉用户你需要该权限的理由，让用户手动打开）
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    protected void onRequestPermissionsResult(Fragment fragment, final int requestCode, @NonNull String[] permissions,
                                              @NonNull int[] grantResults) {
        // 不合法的直接单个授权
        //低版本直接授权 || 不合法的直接单个授权
        if (isIllegalSinglePermission(requestCode)) {
            onPermissionGranted(fragment, grantedListener, requestCode);
            return;
        }
        //权限组内只要有一个被授权，其他的权限也就有了权限，所以使用grantResults[0] == PackageManager.PERMISSION_GRANTED
        if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            onPermissionGranted(fragment, grantedListener, requestCode);
            return;
        }

        String permission = getRequestCodeFromPermission(requestCode);

        //针对小米手机在回调里面处理，如果用户拒绝了这个权限，则打开本应用信息界面，由用户自己手动开启这个权限
        // 如果第二次弹出权限申请的对话框，会出现“以后不再弹出”的提示框，如果用户勾选了，
        // 你再申请权限，则shouldShowRequestPermissionRationale返回true，意思是说要给用户一个 解释，告诉用户为什么要这个权限。
        if (shouldShowRequestPermissionRationale(fragment, permission)) {
            shouldShowPermissionRationale(fragment, Arrays.asList(new String[]{permission}));
        }
    }
}
