package com.j1.permission.handler;

/**
 * Created by wenjing.liu on 17/6/27 in J1.
 * <p>
 * 在请求权限的时，返回对应处理结果。
 * 允许返回给页面进行处理，Denied和ShouldShowRequestFalse采用默认结果
 *
 * @author wenjing.liu
 */


public interface IPermissionResultGrantedListener {
    /***
     /***
     *  权限通过
     * @param requestCode
     */
    void onPermissionGranted(int requestCode);
}
