package com.lvweijie.common.cache;

/**
 * Created by weijie lv on 2019/3/20.in j1
 */

public class MemberCache {
    private String uuID;
    private String mobile;

    public static MemberCache getInstance(){
        return  new MemberCache();
    }
    private MemberCache() {
    }

    public String getUuID() {

        return uuID;
    }

    public void setUuID(String uuID) {
        this.uuID = uuID;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
