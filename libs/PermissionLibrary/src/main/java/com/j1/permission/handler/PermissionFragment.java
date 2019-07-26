package com.j1.permission.handler;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.j1.permission.accountevent.ContactEvent;
import com.j1.permission.log.Log;

/**
 * Created by wenjing.liu on 19/6/12 in J1.
 * 用来处理各种权限
 * 低版本必须在J1Permission进行判断，因为低版本的Fragment中没有对应的onRequestPermissionsResult
 * <p>
 * 一般情况下在检验完权限之后，都会将对应的PermissionFragment移除掉。也可以根据isRemoveAfterCheck来决定是否移除Fragment。例如check account
 *
 * @author wenjing.liu
 */
public class PermissionFragment extends Fragment {

    private FragmentSinglePermissionManager singlePermissionManager;
    private FragmentMultiPermissionsManager multiPermissionsManager;
    private static final String PERMISSION = "permissions";
    private static final String IS_REMOVE = "isRemove";
    private String[] permissions;
    private boolean isRemoveAfterCheck = true;
    private ContactEvent.OnAccountNumberListener numberListener;

    /**
     * 得到一个PermissionFragment实例
     *
     * @param isRemoveAfterCheck 一般情况下check完权限都可以将Fragment移除，但是处理完权限后需要Fragment来处理逻辑的不能移除，例如check account
     * @param permissions
     * @return
     */
    protected static PermissionFragment newInstance(boolean isRemoveAfterCheck, String... permissions) {
        PermissionFragment fragment = new PermissionFragment();
        Bundle bundle = new Bundle();
        bundle.putStringArray(PERMISSION, permissions);
        bundle.putBoolean(IS_REMOVE, isRemoveAfterCheck);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getPermissionsArguments();
        //必须等着创建Fragment,在Fragment的生命周期才能进行校验权限
        requestPermission(permissions);
        Log.w("onAttach = " + this + " ,  per = " + permissions[0]);
    }

    private void getPermissionsArguments() {
        Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }
        permissions = bundle.getStringArray(PERMISSION);
        isRemoveAfterCheck = bundle.getBoolean(IS_REMOVE);
    }

    /**
     * 传入权限监听
     *
     * @param activity
     * @param listener
     * @return
     */
    protected PermissionFragment prepareRequest(Activity activity, IPermissionResultGrantedListener listener) {
        setPermissionListener(listener);
        activity.getFragmentManager().beginTransaction().add(this, getClass().getSimpleName()).commit();
        return this;
    }

    private void setPermissionListener(IPermissionResultGrantedListener listener) {
        if (singlePermissionManager == null) {
            singlePermissionManager = new FragmentSinglePermissionManager();
        }
        if (multiPermissionsManager == null) {
            multiPermissionsManager = new FragmentMultiPermissionsManager();
        }
        singlePermissionManager.setIPermissionResultGrantedListener(listener);
        multiPermissionsManager.setIPermissionResultGrantedListener(listener);
    }

    /**
     * 进行校验权限
     *
     * @param permissions
     */
    private void requestPermission(String[] permissions) {
        //TODO 没有权限需要验证。则验证项目中所有需要的权限,需要获取系统中所有危险的权限进行校验,该代码还没有实现
        if (permissions == null || permissions.length == 0
                || singlePermissionManager == null || multiPermissionsManager == null) {
            //有两个地方会调用到该处：1.调用{@link prepareRequest}；2.调用{@link notifyChanged}。
            // 但是这两个方法在调用的时候，都会singlePermissionManager和multiPermissionsManager进行赋值，所以不知道什么原因（例如Activity在恢复的时候）把该Fragment给加载上去的时候，该权限校验的方法不执行
            return;
        }
        //设置校验完权限是否删除Fragment
        singlePermissionManager.setIsRemoveAfterCheck(isRemoveAfterCheck);
        multiPermissionsManager.setIsRemoveAfterCheck(isRemoveAfterCheck);

        if (permissions.length == 1) {
            singlePermissionManager.requestPermission(this, permissions[0]);
            return;
        }
        multiPermissionsManager.requestMultiPermissions(this, permissions);

    }


    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("onDetach = " + this + " ,  per = " + permissions[0]);
        singlePermissionManager = null;
        multiPermissionsManager = null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FragmentPermissionHandler.CODE_MULTI_PERMISSION) {
            multiPermissionsManager.onRequestMultiPermissionsResult(this, permissions, grantResults);
        } else {
            singlePermissionManager.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        }
    }

    /**
     * 设置联系人信息的监听
     *
     * @param listener
     */
    protected void setOnContactNumberListener(ContactEvent.OnAccountNumberListener listener) {
        this.numberListener = listener;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ContactEvent.REQUEST_ACCOUNT_TO_DETAIL && resultCode == Activity.RESULT_OK) {
            //从选择联系人界面返回
            ContactEvent.startContactDetail(this, data, ContactEvent.REQUEST_ACCOUNT_OF_DETAIL);
        } else if (requestCode == ContactEvent.REQUEST_ACCOUNT_OF_DETAIL) {
            //联系人的详细联系方式
            if (numberListener == null) {
                return;
            }
            if (data == null) {
                numberListener.onAccountNumberListener("", "");
                return;
            }
            Bundle bundle = data.getExtras();
            if (bundle == null) {
                numberListener.onAccountNumberListener("", "");
                return;
            }
            String name = bundle.getString(ContactEvent.CONTACT_NAME);
            String number = bundle.getString(ContactEvent.CONTACT_PHONE);
            numberListener.onAccountNumberListener(name, number);
        }
    }
}
