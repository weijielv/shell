package com.j1.permission.accountevent;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;

import com.j1.permission.handler.J1Permission;

/**
 * Created by wenjing.liu on 19/6/17 in J1.
 *
 * @author wenjing.liu
 */
public class ContactEvent {
    /**
     * 用来跳转进入到联系人的详情页
     */
    public static final int REQUEST_ACCOUNT_TO_DETAIL = 999999;
    public static final int REQUEST_ACCOUNT_OF_DETAIL = 111111;
    public static final String CONTACT_NAME = "contact_name";
    public static final String CONTACT_PHONE = "contact_phone";

    /**
     * 进入到联系人列表页
     *
     * @param fragment
     * @param requestCode
     */
    public static void startPickContact(Fragment fragment, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        fragment.startActivityForResult(intent, requestCode);
    }

    /**
     * 进入到联系人详情页,当响应了checkAccount之后，进入到联系人详情页
     *
     * @param fragment
     * @param data        必须是传入到{@link  J1Permission#permissionAndRequestAccount(int)
     *                    对应返回到{@link Activity#onActivityResult(int, int, Intent)}} 对应的Intent
     * @param requestCode
     */
    public static void startContactDetail(Fragment fragment, Intent data, int requestCode) {
        Uri uri = data.getData();
        Intent intent = new Intent();
        intent.setClass(fragment.getActivity(), ContactDetailActivity.class);
        intent.setData(uri);
        fragment.startActivityForResult(intent, requestCode);
    }

    /**
     * 用来获取联系人的名字和手机号码
     */
    public interface OnAccountNumberListener {
        /**
         * 获取联系人的名字和手机号码
         *
         * @param name
         * @param number
         */
        void onAccountNumberListener(String name, String number);

    }
}
