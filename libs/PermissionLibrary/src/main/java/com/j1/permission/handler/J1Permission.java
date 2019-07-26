package com.j1.permission.handler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.j1.permission.accountevent.ContactEvent;
import com.j1.permission.log.Log;

import java.io.File;


/**
 * Created by wenjing.liu on 19/6/12 in J1.
 * <p>
 * 提供对应的API进行供项目调用
 * <p>
 * 用来校验权限，有以下三种使用方式：
 * <p>
 * 1）最基础的调用
 * <code>
 * J1Permission.create(activity).permission(xxx).request(xxxx);
 * </code>
 * 2）稍微封装一下的调用
 * <code>
 * J1Permission.create(activity).permissionCallPhone().request(xxxx);
 * </code>
 * 3）最简单的调用
 * <code>
 * J1Permission.create(activity).permissionAndRequestXXX(xxx);
 * </code>
 * <p>
 * * @author wenjing.liu
 */
public class J1Permission {
    private Activity activity;
    private String[] permissions;
    private PermissionFragment permissionFragment;


    private J1Permission(Activity activity) {
        this.activity = activity;
    }

    public static J1Permission create(Activity activity) {
        return new J1Permission(activity);
    }

    //##param ####### 自己设置FragmentPermissionHandler中设置的权限 ######## param

    /***
     * 可以自定义的权限
     * @param permission
     * @return
     */
    public J1Permission permission(String... permission) {
        permissions = permission;
        return this;
    }

    /**
     * 可以自定义的权限的请求验证
     *
     * @param listener
     * @return
     */
    public J1Permission request(IPermissionResultGrantedListener listener) {
        return request(listener, true);
    }

    /**
     * 一般情况下在检验完权限之后，都会将对应的PermissionFragment移除掉。也可以根据isRemoveAfterCheck来决定是否移除Fragment
     *
     * @param listener
     * @param isRemoveAfterCheck 一般情况下check完权限都可以将Fragment移除，但是处理完权限后需要Fragment来处理逻辑的不能移除，例如check account
     * @return
     */
    public J1Permission request(IPermissionResultGrantedListener listener, boolean isRemoveAfterCheck) {

        if (permissions == null || permissions.length == 0) {
            throw new IllegalArgumentException("Before call this method , you must call the permission() or permissionXXX()");
        }
        //低版本必须在这里判断，因为低版本的Fragment中没有对应的onRequestPermissionsResult
        if (FragmentPermissionHandler.isLowMVersion()) {
            if (listener == null) {
                return this;
            }
            //根据权限获取对应的requestCode
            int requestCode;
            if (permissions.length == 1) {
                requestCode = FragmentPermissionHandler.getPermissionRequestCode(permissions[0]);
            } else {
                requestCode = FragmentPermissionHandler.CODE_MULTI_PERMISSION;
            }
            listener.onPermissionGranted(requestCode);
            return this;
        }
        //因为在单个Activity实例中完全可能存在多次调用request()，所以一定要将Fragment给移除掉
        //在进行检测权限之前，一定需要将已经存在的Fragment给移除掉,因为有的时候会在检测完权限之后并没有移除PermissionFragment
        FragmentPermissionHandler.removeFragmentFromActivity(activity);
        permissionFragment = PermissionFragment.newInstance(isRemoveAfterCheck, permissions).prepareRequest(activity, listener);
        return this;
    }

    //##param ####### 读取联系人 ----- READ_CONTACTS ######## param

    /**
     * 1.校验读取联系人 READ_CONTACTS
     * 2.需要继续调用{@link #request(IPermissionResultGrantedListener)},只是不需要设置权限了
     * 3.替换调用{@link #permission(String...)}
     *
     * @return
     */
    public J1Permission permissionAccount() {
        permissions = new String[]{FragmentPermissionHandler.PERMISSION_READ_ACCOUNTS};
        return this;
    }

    /**
     * 1.校验读取联系人
     * 2.使用默认的IPermissionResultGrantedListener：直接进入到联系人列表页.
     * 3.不需要调用{@link #permission(String...)}
     *
     * @param numberListener 获取最终的用户名和手机号码
     * @return
     */
    public J1Permission permissionAndRequestAccount(final ContactEvent.OnAccountNumberListener numberListener) {
        permissionAccount();
        request(new IPermissionResultGrantedListener() {
            @Override
            public void onPermissionGranted(int permissionCode) {
                ContactEvent.startPickContact(permissionFragment, ContactEvent.REQUEST_ACCOUNT_TO_DETAIL);
                permissionFragment.setOnContactNumberListener(numberListener);
            }
        }, false);
        return this;
    }
    //#param  ####### 拨打电话 ----- CALL_PHONE ######## #param

    /**
     * 1.读取拨打电话的权限
     * 2.需要继续调用{@link #request(IPermissionResultGrantedListener)}
     * 3.替换调用{@link #permission(String...)}
     *
     * @return
     */
    public J1Permission permissionCallPhone() {
        permissions = new String[]{FragmentPermissionHandler.PERMISSION_CALL_PHONE};
        return this;
    }

    /**
     * 1.读取拨打电话的权限
     * 2.使用默认的IPermissionResultGrantedListener：直接默认的允许回调，回调成功之后弹出拨打电话的对话框
     * 3.不需要调用{@link #permission(String...)}
     *
     * @param phone 需要拨打的手机号码,显示在对话框的电话号码的格式，支持中间加-，如"4007-800-800"，也可以"13795460038"
     * @return
     */
    public J1Permission permissionAndRequestCallPhone(final String phone) {
        permissionCallPhone();
        return request(new IPermissionResultGrantedListener() {
            @Override
            public void onPermissionGranted(int requestCode) {
                Log.d("checkCallPhone, code = " + phone);
                PermissionDialogHandler.callPhoneDialog(activity, phone);
            }
        });
    }

    //##param #######  相机  ----- CAMERA ######## param

    /**
     * 1.读取相机的权限
     * 2.需要继续调用{@link #request(IPermissionResultGrantedListener)}
     * 3.不需要调用{@link #permission(String...)}
     *
     * @return
     */
    public J1Permission permissionCamera() {
        permissions = new String[]{FragmentPermissionHandler.PERMISSION_CAMERA};
        return this;
    }

    /***
     * 1.跳转到目标Activity,检查无误之后直接跳转到对应的页面
     * 2.使用默认的IPermissionResultGrantedListener：直接打开对应的intent对应的Activity
     * 3.不需要调用{@link #permission(String...)}
     *
     * @param targetClass 即将跳转的Activity
     * @param bundle 需要传递过去的bundle对象
     */
    public J1Permission permissionAndRequestCamera(final Class<?> targetClass, final Bundle bundle) {
        permissionCamera();
        return request(new IPermissionResultGrantedListener() {
            @Override
            public void onPermissionGranted(int requestCode) {
                Intent intent = new Intent(activity, targetClass);
                if (bundle != null) {
                    intent.putExtras(bundle);
                }
                activity.startActivity(intent);
            }
        });
    }
    //##param #######  通过相机进行拍照  ----- CAMERA ######## param

    /**
     * 1.通过相机进行拍照的权限
     * 2.需要继续调用{@link #request(IPermissionResultGrantedListener)}
     * 3.不需要调用{@link #permission(String...)}
     *
     * @return
     */
    public J1Permission permissionTakePhotoByCamera() {
        return permissionCamera();
    }

    /**
     * 1.通过相机来拍照片
     * 2.使用默认的IPermissionResultGrantedListener：直接进行拍照，并返回对应的photoRequestCode
     * 3.不需要调用{@link #permission(String...)}
     *
     * @param photoRequestCode 拍照之后返回的onActivityResult的requestCode
     * @param photoName        默认的系统当前时间,可以传空
     * @return 返回的拍照图片的路径
     */
    public String permissionAndRequestTakePhotoByCamera(
            final int photoRequestCode, String photoName) {
        permissionTakePhotoByCamera();
        //没有设置默认的照片的名称，则直接使用默认的系统当前时间
        photoName = TextUtils.isEmpty(photoName) ? String.valueOf(System.currentTimeMillis()) : photoName;
        final File file = new File(activity.getExternalFilesDir(Environment.DIRECTORY_DCIM).getAbsolutePath(), String.format("%s.jpeg", photoName));
        request(new IPermissionResultGrantedListener() {
            @Override
            public void onPermissionGranted(int requestCode) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // 下面这句指定调用相机拍照后的照片存储的路径
                intent.putExtra(MediaStore.EXTRA_OUTPUT, VersionAdapterManager.getUri(activity, file));
                activity.startActivityForResult(intent, photoRequestCode);
            }
        });
        return file.getAbsolutePath();
    }

    //##param #######  从相册中获取图片 ----- WRITE_EXTERNAL_STORAGE######## param


    /**
     * 1.选取相册中的照片
     * 2.使用默认的IPermissionResultGrantedListener：直接进行相册中选图片，并返回对应的photoRequestCode
     * 3.不需要调用{@link #permission(String...)}
     * <p>
     * 为了不在引入 PhotoSelectorLibrary,则直接交给页面处理
     *
     * @param listener
     * @return 返回的拍照图片的路径
     */
    public J1Permission permissionAndRequestPickPhotoFromGallery(
            IPermissionResultGrantedListener listener) {
        permissionWriteStorage();
        return request(listener);
    }


    //##param #######  麦克风 ----- RECORD_AUDIO ######## param

    /**
     * 1.读取麦克风的权限
     * 2.需要继续调用{@link #request(IPermissionResultGrantedListener)}
     * 3.不需要调用{@link #permission(String...)}
     *
     * @return
     */
    public J1Permission permissionRecordAudio() {
        permissions = new String[]{FragmentPermissionHandler.PERMISSION_RECORD_AUDIO};
        return this;
    }

    /**
     * 1.麦克风,检查无误之后直接跳转到对应的页面
     * 2.使用默认的IPermissionResultGrantedListener：直接打开对应的intent对应的Activity
     * 3.不需要调用{@link #permission(String...)}
     *
     * @param targetClass 即将跳转的Activity
     * @param bundle      需要传递过去的bundle对象
     */
    public J1Permission permissionAndRequestRecordAudio(final Class<?> targetClass, final Bundle bundle) {
        permissionRecordAudio();
        return request(new IPermissionResultGrantedListener() {
            @Override
            public void onPermissionGranted(int requestCode) {
                Intent intent = new Intent(activity, targetClass);
                if (bundle != null) {
                    intent.putExtras(bundle);
                }
                activity.startActivity(intent);
            }
        });
    }


    //#param ####### 手机的内部存储------ WRITE_EXTERNAL_STORAGE ####### #param

    /**
     * 1.读取手机的内部存储的权限
     * 2.需要继续调用{@link #request(IPermissionResultGrantedListener)}
     * 3.不需要调用{@link #permission(String...)}
     *
     * @return
     */
    public J1Permission permissionWriteStorage() {
        permissions = new String[]{FragmentPermissionHandler.PERMISSION_WRITE_EXTERNAL_STORAGE};
        return this;
    }

    //#param ####### 手机的IMSI------ READ_PHONE_STATE ####### #param

    /**
     * 1.读取手机的IMSI权限
     * 2.需要继续调用{@link #request(IPermissionResultGrantedListener)}
     * 3.不需要调用{@link #permission(String...)}
     *
     * @return
     */
    public J1Permission permissionReadPhoneState() {
        permissions = new String[]{FragmentPermissionHandler.PERMISSION_READ_PHONE_STATE};
        return this;
    }

    //#param ####### 手机的定位------ READ_PHONE_STATE ####### #param

    /**
     * 1.读取手机的定位权限
     * 2.需要继续调用{@link #request(IPermissionResultGrantedListener)}
     * 3.不需要调用{@link #permission(String...)}
     *
     * @return
     */
    public J1Permission permissionLocation() {
        permissions = new String[]{FragmentPermissionHandler.PERMISSION_ACCESS_FINE_LOCATION};
        return this;
    }

}
