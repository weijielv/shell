package com.j1.j1finger;

import android.app.Activity;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.security.keystore.KeyInfo;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.j1.j1finger.utils.LoaclSPUtil;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;

import static android.content.Context.FINGERPRINT_SERVICE;
import static android.security.keystore.KeyProperties.PURPOSE_DECRYPT;
import static android.security.keystore.KeyProperties.PURPOSE_ENCRYPT;
import static com.j1.j1finger.utils.LoaclSPUtil.IVNAME;

/**
 * Created by weijie lv on 2019/4/12.in j1
 */

public class FingerManager {

    private static FingerManager fingerManager;
    private Activity activity;
    FingerprintManager manager;
    private String TAG = "FingerManager";
    private FingerAuthenticationCallBack callback;

    LoaclSPUtil loaclSPUtil;


    private FingerManager() {
        manager = (FingerprintManager) activity.getSystemService(FINGERPRINT_SERVICE);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private FingerManager(Activity activity) {
        manager = (FingerprintManager) activity.getSystemService(FINGERPRINT_SERVICE);
        this.activity = activity;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static FingerManager getInstance(Activity activity) {
        if (fingerManager == null) {
            fingerManager = new FingerManager(activity);

        }
        return fingerManager;
    }

    /**
     * 获取当前设备的指纹状态 * * @param ctx * @return 是否支持指纹
     */
    public static FingerPrintStatus getFingerprintAvailable(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return FingerPrintStatus.NotSupport;
        }
        FingerprintManager manager = (FingerprintManager) context.getSystemService(FINGERPRINT_SERVICE);
        if (!manager.isHardwareDetected()) {
            return FingerPrintStatus.NO_FINGER_HARDWARE;
        }
        //检测手机
        if (isKeyProtectedEnforcedBySecureHardware()) {
            return FingerPrintStatus.NO_FINGER_HARDWARE;
        }
        if (!manager.hasEnrolledFingerprints()) {
            return FingerPrintStatus.NONE_FINGER;
        }
        return FingerPrintStatus.HAS_FINGER;
    }

    /**
     * 请求指纹验证。
     * @param purpose PURPOSE_DECRYPT or  PURPOSE_ENCRYPT  解密数据或者加密数据。
     * @param kv        加密是续传入key value. key为存储的名字。
     *                  解密只需要传入key即可。通过key取出
     * @param callback  验证通过后的回调。
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void requestFinger(int purpose, @Nullable J1KeyValue<String ,String > kv, FingerAuthenticationCallBack callback) throws CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, IOException, KeyStoreException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        this.callback = callback;
        FingerprintManager manager = (FingerprintManager) activity.getSystemService(FINGERPRINT_SERVICE);
        try {
            setPurpose(purpose);
            setSecretData(kv);
            FingerprintManager.CryptoObject crypto =
                    J1CryptoObjectCreator.getInstance(activity).creatCryptoObject(purpose,kv.getKey());

            manager.authenticate(crypto, mCancellationSignal, 0, mSelfCancelled, null);
        }
        catch (KeyPermanentlyInvalidatedException e) {
            //e.printStackTrace();
            //检测到系统指纹的变更。  如何能提前获得？
            callback.onAuthenticationFail("检测到指纹变更,重新使用密码验证。");
            //int purpose = PURPOSE_ENCRYPT;
            /*setPurpose(PURPOSE_ENCRYPT);
            setSecretData("123741");
            final Cipher cipher = initCipher(PURPOSE_ENCRYPT);
            if (cipher == null) {
                callback.onAuthenticationFail("cipher = null");
                return;
            }
            FingerprintManager.CryptoObject crypto = new FingerprintManager.CryptoObject(cipher);

            manager.authenticate(crypto, mCancellationSignal, 0, mSelfCancelled, null);*/

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断是否已经设置了指纹登陆
     * @return PURPOSE_ENCRYPT 加密。 or   PURPOSE_DECRYPT 解密
     */
    public int isOpenFingerLogin(String alias){
        loaclSPUtil  =new LoaclSPUtil(activity);
        int purpose = TextUtils.isEmpty(loaclSPUtil.getString(alias))? PURPOSE_ENCRYPT : PURPOSE_DECRYPT;
        return purpose;
    }

    CancellationSignal mCancellationSignal = new CancellationSignal();

    /*
    指纹验证的回调。
     */
    FingerprintManager.AuthenticationCallback mSelfCancelled = new FingerprintManager.AuthenticationCallback() {
        @Override
        public void onAuthenticationError(int errorCode, CharSequence errString) {
            //但多次指纹密码验证错误后，进入此方法；并且，不能短时间内调用指纹验证
            //Toast.makeText(activity, "尝试次数过多，请稍后重试", Toast.LENGTH_LONG).show();
            callback.onAuthenticationError(errorCode, errString);
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
            super.onAuthenticationSucceeded(result);
            //去除cipher 加密或者解密
            if (result.getCryptoObject() == null) {
                callback.onAuthenticationFail("没有加密方");
                return;
            }
            if (result.getCryptoObject().getCipher() == null) {
                callback.onAuthenticationFail("没有加密cipher");
                return;
            }
            Cipher cipher = result.getCryptoObject().getCipher();
            Log.e(TAG,"purpose = "+ getPurpose());
            if (getPurpose() == PURPOSE_DECRYPT) {
                String data = loaclSPUtil.getString(getSecretData().getKey());
                if (TextUtils.isEmpty(data)){
                    callback.onAuthenticationFail("没有获取解密数据");
                    return;
                }
                decodeData(cipher, data);
            } else {
                //将前面生成的data包装成secret key，存入沙盒
                String data = getSecretData().getValue();
                if (TextUtils.isEmpty(data)){
                    callback.onAuthenticationFail("加密失败：没有加密数据");
                    return;
                }
                encodeData(cipher, getSecretData());
            }
        }

        @Override
        public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
            super.onAuthenticationHelp(helpCode, helpString);
            callback.onAuthenticationHelp(helpCode,helpString);
        }
    };

    /**
     * 通通过指纹验证后，开始解密过程。
     * @param cipher     通过指纹验证后拿到的cipher。
     * @param data       已经加密过的数据。
     */
    private void decodeData(Cipher cipher, String data) {
        //取出secret key并返回
        if (TextUtils.isEmpty(data)) {
            callback.onAuthenticationFail("没有获得要加密数据");
            return;
        }
        try {
            byte[] basebyte = Base64.decode(data, Base64.URL_SAFE);
            try {
                byte[] decrypted = cipher.doFinal(basebyte);
                callback.onAuthenticationSucceeded(new String(decrypted));
            }catch (Exception e){
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
            callback.onAuthenticationFail(e.getMessage());
        }
    }

    private void encodeData(Cipher cipher, J1KeyValue<String ,String> kv) {
        String data = kv.getValue();
        try {
            byte[] encrypted = cipher.doFinal(data.getBytes());
            byte[] IV = cipher.getIV();
            String encrypted_bs64 = Base64.encodeToString(encrypted, Base64.URL_SAFE | Base64.NO_WRAP);
            String iv_bs64 = Base64.encodeToString(IV, Base64.URL_SAFE);
            Log.e(TAG, "se == " + encrypted_bs64);
            Log.e(TAG, "iv_bs64 == " + iv_bs64);
            if (loaclSPUtil.putString(kv.getKey(), encrypted_bs64)
                    && loaclSPUtil.putString(kv.getKey()+IVNAME, iv_bs64)) {
                callback.onAuthenticationSucceeded(encrypted_bs64);
            } else {
                callback.onAuthenticationFail("没有保存成功");
            }
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
            callback.onAuthenticationFail("解密失败");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public  static boolean isKeyProtectedEnforcedBySecureHardware() {
        try {
            //这里随便生成一个key，检查是不是受保护即可
            KeyStore mStore = KeyStore.getInstance("AndroidKeyStore");
            final SecretKey key = (SecretKey) mStore.getKey("key", null);
            if (key == null) {
                return false;
            }
            SecretKeyFactory factory = SecretKeyFactory.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            KeyInfo keyInfo;
            keyInfo = (KeyInfo) factory.getKeySpec(key, KeyInfo.class);
            return keyInfo.isInsideSecureHardware() && keyInfo.isUserAuthenticationRequirementEnforcedBySecureHardware();
        } catch (Exception e) {
            // Not an Android KeyStore key.
            return false;
        }
    }


    private J1KeyValue<String ,String>  secretData;
    public J1KeyValue<String ,String> getSecretData() {
        return secretData;
    }
    public void setSecretData(J1KeyValue<String ,String>  secretData) {
        this.secretData = secretData;
    }

    private int purpose;

    private int getPurpose() {
        return purpose;
    }

    private void setPurpose(int purpose) {
        this.purpose = purpose;
    }
}