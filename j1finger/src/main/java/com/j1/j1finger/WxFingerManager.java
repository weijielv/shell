package com.j1.j1finger;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.tencent.soter.wrapper.SoterWrapperApi;
import com.tencent.soter.wrapper.wrap_callback.SoterProcessAuthenticationResult;
import com.tencent.soter.wrapper.wrap_callback.SoterProcessCallback;
import com.tencent.soter.wrapper.wrap_callback.SoterProcessKeyPreparationResult;
import com.tencent.soter.wrapper.wrap_callback.SoterProcessNoExtResult;
import com.tencent.soter.wrapper.wrap_fingerprint.SoterFingerprintCanceller;
import com.tencent.soter.wrapper.wrap_fingerprint.SoterFingerprintStateCallback;
import com.tencent.soter.wrapper.wrap_task.AuthenticationParam;
import com.tencent.soter.wrapper.wrap_task.InitializeParam;

/**
 * Created by weijie lv on 2019/4/22.in j1
 *
 * 微信soter demo，华为nova3 暂不支持。
 * device not support soter
 */

public class WxFingerManager {
    String TAG = "WxFingerManager";

    public void initParam(Context context) {
        InitializeParam param = new InitializeParam.InitializeParamBuilder()
                .setScenes(0) // 场景值常量，后续使用该常量进行密钥生成或指纹认证
                .build();
        SoterWrapperApi.init(context,
                new SoterProcessCallback<SoterProcessNoExtResult>() {
                    /**
                     * Called when the process come to the end, regardless it's done, there's a failure or user cancelled it.
                     *
                     * @param result The result of the process
                     */
                    @Override
                    public void onResult(@NonNull SoterProcessNoExtResult result) {
                        Log.e(TAG,"init");
                    }
                },
                param);
    }


    public void createKey() {
        SoterWrapperApi.prepareAuthKey(new SoterProcessCallback<SoterProcessKeyPreparationResult>() {
            /**
             * Called when the process come to the end, regardless it's done, there's a failure or user cancelled it.
             *
             * @param result The result of the process
             */
            @Override
            public void onResult(@NonNull SoterProcessKeyPreparationResult result) {
                Log.e(TAG,"prepareAuthKey");
            }
        }, false, true, 0, null, null);

    }


    public void requestAuth(Context context) {
        initParam(context);
        createKey();
        AuthenticationParam param = new AuthenticationParam.AuthenticationParamBuilder()
                .setScene(0)
                .setContext(context)
                .setFingerprintCanceller(new SoterFingerprintCanceller())
                .setPrefilledChallenge("test challenge")
                .setSoterFingerprintStateCallback(new SoterFingerprintStateCallback() {
                    /**
                     * Callback when fingerprint sensor start listening
                     */
                    @Override
                    public void onStartAuthentication() {
                        Log.e(TAG,"onStartAuthentication");
                    }

                    /**
                     * Callback when sensor indicates this authentication event as not success, neither not error, e.g., user authenticate with
                     * wet fingerprint.
                     *
                     * @param helpCode   The help code provided by system
                     * @param helpString The hint msg provided by system
                     */
                    @Override
                    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {

                    }

                    /**
                     * Callback when sensor indicates this authentication event as success.
                     */
                    @Override
                    public void onAuthenticationSucceed() {
                        Log.e(TAG,"onAuthenticationSucceed");
                    }

                    /**
                     * Callback when sensor indicates this authentication event as failed, which means user uses a not enrolled fingerprint
                     * for authentication
                     */
                    @Override
                    public void onAuthenticationFailed() {
                        Log.e(TAG,"onAuthenticationFailed");
                    }

                    /**
                     * Callback when user cancelled the authentication
                     */
                    @Override
                    public void onAuthenticationCancelled() {
                        Log.e(TAG,"onAuthenticationCancelled");
                    }

                    /**
                     * Callback when sensor indicates this authentication event as an unrecoverable error, e.g., Auth Key is invalid permanently
                     * Note that we separate cancellation event from it and move it to {@link SoterFingerprintStateCallback#onAuthenticationFailed()}, which
                     * we think is much more reasonable
                     *
                     * @param errorCode   The error code provided by system
                     * @param errorString The hint msg provided by system
                     */
                    @Override
                    public void onAuthenticationError(int errorCode, CharSequence errorString) {

                    }
                }).build();
        SoterWrapperApi.requestAuthorizeAndSign(new SoterProcessCallback<SoterProcessAuthenticationResult>() {
            /**
             * Called when the process come to the end, regardless it's done, there's a failure or user cancelled it.
             *
             * @param result The result of the process
             */
            @Override
            public void onResult(@NonNull SoterProcessAuthenticationResult result) {
                Log.e(TAG,"key 生成了= "+result.getErrMsg());
            }
        }, param);
    }

}
