package com.j1.j1finger;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import android.os.CancellationSignal;
import android.util.Log;

/**
 * Created by weijie lv on 2019/4/24.in j1
 */

public class BiometricManager {

    private BiometricPrompt mBiometricPrompt;
    private CancellationSignal mCancellationSignal;
    private BiometricPrompt.AuthenticationCallback mAuthenticationCallback;
    private String TAG = "BiometricManager";


    @TargetApi(28)
    public void init(Context context) {
        mBiometricPrompt = new BiometricPrompt.Builder(context)
                .setTitle("指纹验证")
                .setDescription("描述")
                .setNegativeButton("取消", context.getMainExecutor(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.i(TAG, "Cancel button clicked");
                    }
                })
                .build();
        mCancellationSignal = new CancellationSignal();
        mCancellationSignal.setOnCancelListener(new CancellationSignal.OnCancelListener() {
            @Override
            public void onCancel() {
                //handle cancel result
                Log.i(TAG, "Canceled");
            }
        });
        mAuthenticationCallback = new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);

                Log.i(TAG, "onAuthenticationError " + errString);
            }

            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);

                Log.i(TAG, "onAuthenticationSucceeded " + result.toString());
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();

                Log.i(TAG, "onAuthenticationFailed ");
            }
        };


        BiometricPrompt.CryptoObject cryptoObject = J1CryptoObjectCreator.getInstance(context).createBiometricCryptoObject(1, "");
        mBiometricPrompt.authenticate(cryptoObject,mCancellationSignal, context.getMainExecutor(), mAuthenticationCallback);
    }

    public void cancellationSignal(){
        mCancellationSignal.cancel();
    }
}
