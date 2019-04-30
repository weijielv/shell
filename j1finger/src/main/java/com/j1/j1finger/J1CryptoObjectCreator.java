package com.j1.j1finger;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.biometrics.BiometricPrompt;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.security.keystore.KeyProperties;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Base64;
import android.util.Log;

import com.j1.j1finger.utils.IShaKeyGenerater;
import com.j1.j1finger.utils.ISymmetricKeyGenerater;
import com.j1.j1finger.utils.KeyGenerateImp;
import com.j1.j1finger.utils.LocalSPUtil;

import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import static android.security.keystore.KeyProperties.PURPOSE_DECRYPT;
import static com.j1.j1finger.utils.LocalSPUtil.IV_NAME;

/**
 * Created by weijie lv on 2019/4/16.in j1
 * 加密和解密 cipher的初始化。
 */

public class J1CryptoObjectCreator {

    private KeyGenerateImp keyGenerateImp;
    LocalSPUtil localSPUtil;
    public static J1CryptoObjectCreator cryptoObjectCreator = null;
    private String TAG = "J1CryptoObjectCreator";

    private J1CryptoObjectCreator(Context context) {
        //keyGenerateImp = new ISymmetricKeyGenerater();
        keyGenerateImp = new ISymmetricKeyGenerater();
        localSPUtil = new LocalSPUtil(context);
    }

    ;

    public static J1CryptoObjectCreator getInstance(Context context) {

        if (cryptoObjectCreator == null) {
            cryptoObjectCreator = new J1CryptoObjectCreator(context);
        }
        return cryptoObjectCreator;
    }

    /**
     * api 为23到28之间调用
     *
     * @param purpose
     * @param keyStoreAlias
     * @return
     */
    @TargetApi(Build.VERSION_CODES.M)
    public FingerprintManager.CryptoObject createCryptoObject(int purpose, String keyStoreAlias) {
        Cipher cipher = initCipher(purpose, keyStoreAlias);
        FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
        return cryptoObject;

    }

    /**
     * 当api大于28后，生成类型为 BiometricPrompt.CryptoObject
     *
     * @param purpose
     * @param keyStoreAlias
     * @return
     */
    @RequiresApi(api = 28)
    public BiometricPrompt.CryptoObject createBiometricCryptoObject(int purpose, String keyStoreAlias) {
        Cipher cipher = initCipher(purpose, keyStoreAlias);
        BiometricPrompt.CryptoObject cryptoObject = new BiometricPrompt.CryptoObject(cipher);
        return cryptoObject;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private Cipher initCipher(int purpose, String keyStoreAlias) {
        Cipher cipher = null;
        try {
            //加密算法。
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            if (purpose == PURPOSE_DECRYPT) {//解密
                Key key = keyGenerateImp.getSecretKey(keyStoreAlias);
                if (key == null) {
                    return null;
                }
                //通过密钥加密的数据，保存在文件中，现在拿出来，解密出来
                String iv = localSPUtil.getString(keyStoreAlias + IV_NAME);
                byte[] mIV = Base64.decode(iv, Base64.URL_SAFE);
                //初始化解密cipher
                cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(mIV));
            } else {//加密
                //通过KeyStore生成密钥。
                keyGenerateImp.generateKey(keyStoreAlias);
                //拿到key用于初始化 cipher。
                Key key = keyGenerateImp.getSecretKey(keyStoreAlias);
                if (key == null) {
                    return null;
                }
                //使用密钥初始化加密cipher。
                cipher.init(Cipher.ENCRYPT_MODE, key);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cipher;
    }
}
