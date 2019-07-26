package com.j1.permission.handler;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;

import com.j1.permission.R;

import java.util.Arrays;
import java.util.List;

/**
 * Created by wenjing.liu on 19/4/17 in J1.
 * 比较好的一个框架：https://www.jianshu.com/p/6a4dff744031
 * Android 8.0 之前的版本，同一组的任何一个权限被授权了，组内的其他权限也自动被授权，
 * 但是Android 8.0之后的版本，需要更明确指定所使用的权限，并且系统只会授予申请的权限，
 * 不会授予没有组内的其他权限，这意味着，如果只申请了外部存储空间读取权限，
 * 在低版本下（API < 26）对外部存储空间使用写入操作是没有问题的，
 * 但是在高版本（API >= 26）下是会出现问题的，解决方案是需要两个将读和写的权限一起申请
 *
 * @author wenjing.liu
 */
public abstract class FragmentPermissionHandler extends PermissionDialogHandler {
    /**
     * 对应REQUEST_PERMISSION的权限名称
     */
    public static final int CODE_READ_ACCOUNTS = 0;
    public static final int CODE_CALL_PHONE = 1;
    public static final int CODE_READ_PHONE_STATE = 2;
    public static final int CODE_CAMERA = 3;
    public static final int CODE_ACCESS_FINE_LOCATION = 4;
    public static final int CODE_RECORD_AUDIO = 5;
    public static final int CODE_WRITE_EXTERNAL_STORAGE = 6;
    public static final int CODE_READ_SMS = 7;


    public static final int CODE_MULTI_PERMISSION = 100;


    /**
     * 6.0权限的基本知识，以下是需要单独申请的权限，共分为9组，每组只要有一个权限申请成功了，就默认整组权限都可以使用了。
     * group.CONTACTS
     */
    public static final String PERMISSION_READ_ACCOUNTS = Manifest.permission.READ_CONTACTS;
    /**
     * group.PHONE
     */
    public static final String PERMISSION_CALL_PHONE = Manifest.permission.CALL_PHONE;
    public static final String PERMISSION_READ_PHONE_STATE = Manifest.permission.READ_PHONE_STATE;
    /**
     * group.CAMERA
     */
    public static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
    /**
     * group.LOCATION
     */
    public static final String PERMISSION_ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    /**
     * group.MICROPHONE
     */
    public static final String PERMISSION_RECORD_AUDIO = Manifest.permission.RECORD_AUDIO;
    /**
     * group.STORAGE
     */
    public static final String PERMISSION_WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    /**
     * group.SMS
     */
    public static final String PERMISSION_READ_SMS = Manifest.permission.READ_SMS;


    protected static final String[] REQUEST_PERMISSION = {
            PERMISSION_READ_ACCOUNTS,
            PERMISSION_CALL_PHONE,
            PERMISSION_READ_PHONE_STATE,
            PERMISSION_CAMERA,
            PERMISSION_ACCESS_FINE_LOCATION,
            PERMISSION_RECORD_AUDIO,
            PERMISSION_WRITE_EXTERNAL_STORAGE,
            PERMISSION_READ_SMS
    };

    private boolean isRemoveAfterCheck = true;

    /**
     * @param isRemoveAfterCheck 一般情况下check完权限都可以将Fragment移除，但是处理完权限后需要Fragment来处理逻辑的不能移除，例如check account
     */
    protected void setIsRemoveAfterCheck(boolean isRemoveAfterCheck) {
        this.isRemoveAfterCheck = isRemoveAfterCheck;
    }

    /***
     *  http://blog.csdn.net/hudashi/article/details/50775180
     * Android原生系统中，如果第二次弹出权限申请的对话框，会出现“以后不再弹出”的提示框，如果用户勾选了，你再申请权限，则shouldShowRequestPermissionRationale返回true，意思是说要给用户一个 解释，告诉用户为什么要这个权限。
     * 然而，在实际开发中，需要注意的是，很多手机对原生系统做了修改，比如小米，小米4的6.0的shouldShowRequestPermissionRationale 就一直返回false，而且在申请权限时，如果用户选择了拒绝，则不会再弹出对话框了。。。。
     * 所以说这个地方有坑，我的解决方法是，在回调里面处理，如果用户拒绝了这个权限，则打开本应用信息界面，由用户自己手动开启这个权限
     * （第二种方案：在onRequestPermissionsResult函数中进行检测，如果返回PERMISSION_DENIED，则去调用shouldShowRequestPermissionRationale函数，如果返回false代表用户已经禁止该权限（上面的3和4两种情况），弹出dialog告诉用户你需要该权限的理由，让用户手动打开）
     * @param fragment
     * @param shouldPermissions
     */

    protected void shouldShowPermissionRationale(Fragment fragment, List<String> shouldPermissions) {
        onConfirmDeniedAgain(fragment.getActivity(), shouldPermissions);
        removeFragment(fragment);
    }

    /***
     * 权限被允许
     * @param permissionResult
     * @param requestCode
     */
    protected void onPermissionGranted(Fragment fragment, IPermissionResultGrantedListener permissionResult, int requestCode) {
        removeFragment(fragment);
        if (permissionResult == null) {
            return;
        }
        permissionResult.onPermissionGranted(requestCode);
    }

    /**
     * 若该Fragment已经添加到Activity中，则需要移除该Fragment
     *
     * @param fragment
     */
    protected void removeFragment(Fragment fragment) {
        if (fragment == null || !isRemoveAfterCheck) {
            return;
        }
        FragmentTransaction ft = fragment.getFragmentManager().beginTransaction();
        ft.remove(fragment);
        ft.commit();
    }


    /**
     * 低版本的包装类
     * 如果第二次弹出权限申请的对话框，会出现“以后不再弹出”的提示框，如果用户勾选了，
     * 你再申请权限，则shouldShowRequestPermissionRationale返回true，意思是说要给用户一个 解释，告诉用户为什么要这个权限。
     *
     * @param fragment
     * @param permission
     * @return
     */
    protected boolean shouldShowRequestPermissionRationale(Fragment fragment, String permission) {
        if (isLowMVersion()) {
            return false;
        }
        return fragment.shouldShowRequestPermissionRationale(permission);
    }

    protected int checkSelfPermission(Fragment fragment, String permission) {
        if (isLowMVersion()) {
            return PackageManager.PERMISSION_GRANTED;
        }
        return fragment.getContext().checkSelfPermission(permission);
    }

    /***
     * 低版本的包装类
     * 1、请求权限后，系统会弹出请求权限的Dialog,用户选择允许或拒绝后，会回调onRequestPermissionsResult方法, 该方法类似于onActivityResult
     * 2、在Fragment中申请权限，不要使用ActivityCompat.requestPermissions,
     * 直接使用Fragment的requestPermissions方法，否则会回调到Activity的onRequestPermissionsResult
     *3、如果在Fragment中嵌套Fragment，在子Fragment中使用requestPermissions方法，onRequestPermissionsResult不会回调回来，
     *建议使用getParentFragment().requestPermissions方法，
     *这个方法会回调到父Fragment中的onRequestPermissionsResult，加入以下代码可以把回调透传到子Fragment
     * @param fragment
     * @param permissions
     * @param requestCode
     */
    protected void requestPermissions(Fragment fragment, String[] permissions, int requestCode) {
        if (isLowMVersion()) {
            return;
        }
        fragment.requestPermissions(permissions, requestCode);
    }

    /**
     * 根据权限换取对应的requestCode
     *
     * @param permission
     * @return
     */
    protected static int getPermissionRequestCode(String permission) {
        return Arrays.asList(REQUEST_PERMISSION).indexOf(permission);
    }

    /**
     * 根据requestCode换取对应的权限
     *
     * @param requestCode
     * @return
     */
    protected String getRequestCodeFromPermission(int requestCode) {
        if (requestCode >= REQUEST_PERMISSION.length) {
            return "";
        }
        return REQUEST_PERMISSION[requestCode];
    }


    /***
     * 根据requestCode获取对应的权限提示
     * @param activity
     * @param permissions
     * @return
     */
    private String getPermissionDetail(Activity activity, List<String> permissions) {
        String[] infos = activity.getResources().getStringArray(R.array.permission);
        StringBuffer buffer = new StringBuffer();
        if (permissions == null || permissions.size() == 0) {
            return "";
        }

        for (String permission : permissions) {
            for (int i = 0; i < REQUEST_PERMISSION.length; i++) {
                if (permission.equals(REQUEST_PERMISSION[i])) {
                    buffer.append(infos[i]);
                    buffer.append(",");
                    break;
                }

            }

        }
        String result = buffer.toString();
        if (result.length() > 0 && result.endsWith(",")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    /**
     * 再一次提示用户开启权限
     *
     * @param activity
     * @param permissions
     */
    private void onConfirmDeniedAgain(Activity activity, List<String> permissions) {
        String permission = getPermissionDetail(activity, permissions);
        openSettingActivityDialog(activity, permission);
    }


    /**
     * 打开设置界面
     *
     * @param activity
     * @param permission
     */
    private void openSettingActivityDialog(Activity activity, String permission) {
        String message = String.format(activity.getString(R.string.perm_message), permission);
        String title = String.format(activity.getString(R.string.perm_title), permission);
        openSettingActivityDialog(activity, title, message);
    }

    /***
     * 打开设置界面
     * @param activity
     * @param title
     * @param message
     */
    private void openSettingActivityDialog(final Activity activity, String title, String message) {
        //提示用户打开setting界面
        showMessageDialog(activity, title, message, R.string.perm_open, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PermissionSettingActivity.startSettingActivity(activity);

                dialog.dismiss();
            }
        });
    }

    /**
     * 将已存在的PermissionFragment从Activity中移除掉
     *
     * @param activity
     */
    protected static void removeFragmentFromActivity(Activity activity) {
        if (isLowMVersion()) {
            return;
        }
        Fragment fragment = activity.getFragmentManager().findFragmentByTag(PermissionFragment.class.getSimpleName());
        if (fragment == null) {
            return;
        }
        if (fragment instanceof PermissionFragment) {
            FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
            ft.remove(fragment);
            ft.commit();
        }
    }

    /***
     * 是否低于M 版本
     * @return
     */
    protected static boolean isLowMVersion() {
        //低版本不去进行校验
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M;
    }

    /**
     * 非法的权限
     *
     * @param requestCode
     * @return
     */
    protected boolean isIllegalSinglePermission(int requestCode) {
        return (requestCode < 0 || requestCode >= REQUEST_PERMISSION.length) || requestCode == CODE_MULTI_PERMISSION;
    }
}

