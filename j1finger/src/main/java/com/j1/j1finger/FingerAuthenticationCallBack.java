package com.j1.j1finger;

/**
 * Created by weijie lv on 2019/4/17.in j1
 */

public interface FingerAuthenticationCallBack {
    void onAuthenticationFail(String result);

    void onAuthenticationSucceeded(String result);

    void onAuthenticationError(int errorCode, CharSequence errString);

    void onAuthenticationHelp(int helpCode, CharSequence helpString);
}
