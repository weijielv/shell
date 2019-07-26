package com.j1.permission.handler;

import android.app.Fragment;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wenjing.liu on 19/4/17 in J1.
 * <p>
 * 处理多个权限的校验和验证结果
 *
 * @author wenjing.liu
 */
public class FragmentMultiPermissionsManager extends FragmentPermissionHandler {

    private IPermissionResultGrantedListener grantedListener;

    protected FragmentMultiPermissionsManager() {
    }


    protected void setIPermissionResultGrantedListener(IPermissionResultGrantedListener listener) {
        this.grantedListener = listener;
    }

    /***
     * 一次申请多个权限
     * @param permissionCodes
     */

    protected void requestMultiPermissions(Fragment fragment, String[] permissionCodes) {

        //低版本 || 不合法
        if ( isIllegalMultiPermission(permissionCodes)) {
            onPermissionGranted(fragment,grantedListener, CODE_MULTI_PERMISSION);
            return;
        }

        //找出里面的需要check、需要二次确认的
        List<String> shouldPermissions = new ArrayList<>();
        List<String> checkPermissions = new ArrayList<>();
        for (int i = 0; i < permissionCodes.length; i++) {
            //获取该权限
            String permission = permissionCodes[i];
            //从权限中获取对应的requestCode
            int code = getPermissionRequestCode(permission);
            if (code <= 0 || code >= REQUEST_PERMISSION.length) {
                continue;

            }
            //进行检测权限
            int checkSelfPermission = checkSelfPermission(fragment, permission);
            if (checkSelfPermission == PackageManager.PERMISSION_GRANTED) {
                continue;
            }
            if (shouldShowRequestPermissionRationale(fragment, permission)) {
                shouldPermissions.add(permission);
                continue;
            }
            //需要校验权限的
            checkPermissions.add(permission);
        }

        if (shouldPermissions.size() == 0 && checkPermissions.size() == 0) {
            onPermissionGranted(fragment,grantedListener, CODE_MULTI_PERMISSION);
            return;
        }
        //如果所有的权限都被校验过一次，则直接提示
        if (checkPermissions.size() == 0 && shouldPermissions.size() != 0) {
            shouldShowPermissionRationale(fragment, shouldPermissions);
            return;
        }
        //否则直接把所有的权限作为没有被校验过的权限进行处理,将校验过的权限和没有校验过的权限一起进行重新校验加到checkPermissions中
        checkPermissions.addAll(shouldPermissions);
        requestPermissions(fragment, checkPermissions.toArray(new String[checkPermissions.size()]), CODE_MULTI_PERMISSION);
    }

    /***
     * 处理多个申请权限的
     * @param permissions
     * @param grantResults
     */
    protected void onRequestMultiPermissionsResult(Fragment fragment, @NonNull String[] permissions, @NonNull int[] grantResults) {

        List<String> checkPermissions = new ArrayList<>();
        List<String> shouldPermissions = new ArrayList<>();
        //循环这些权限里面的允许的权限和第二次确认的权限
        for (int i = 0; i < permissions.length; i++) {
            String permission = permissions[i];
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                continue;
            }
            if (!shouldShowRequestPermissionRationale(fragment, permission)) {
                shouldPermissions.add(permission);
                continue;
            }
            checkPermissions.add(permission);
        }
        //如果所有的权限都被校验过一次，则直接提示
        if (shouldPermissions.size() == 0 && checkPermissions.size() == 0) {
            onPermissionGranted(fragment,grantedListener, CODE_MULTI_PERMISSION);
            return;
        }
        //如果所有的权限都被校验过一次，则直接提示
        if (checkPermissions.size() == 0 && shouldPermissions.size() != 0) {
            shouldShowPermissionRationale(fragment, shouldPermissions);
        }
    }

    /**
     * 非法的权限
     *
     * @param permissionCodes
     * @return
     */
    private boolean isIllegalMultiPermission(String[] permissionCodes) {
        return permissionCodes == null || permissionCodes.length == 0;
    }

}
