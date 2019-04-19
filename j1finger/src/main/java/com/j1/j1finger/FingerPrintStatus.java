package com.j1.j1finger;

/**
 * Created by weijie lv on 2019/4/12.in j1
 * 用户手机关于指纹的硬件状态和指纹状态。
 */

public enum FingerPrintStatus {
    //手机系统不支持 小于6.0   23
    NotSupport,
    //没有指纹硬件
    NO_FINGER_HARDWARE,
    //有可用指纹
    HAS_FINGER,
    //没有可用的指纹
    NONE_FINGER
}
