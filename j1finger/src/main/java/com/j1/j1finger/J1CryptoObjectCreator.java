package com.j1.j1finger;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.security.keystore.KeyProperties;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Base64;

import com.j1.j1finger.utils.ISymmetricKeyGenerater;
import com.j1.j1finger.utils.KeyGenerateImp;
import com.j1.j1finger.utils.LocalSPUtil;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import static android.security.keystore.KeyProperties.PURPOSE_DECRYPT;
import static com.j1.j1finger.utils.LocalSPUtil.IV_NAME;

/**
 * Created by weijie lv on 2019/4/16.in j1
 * 加密和解密 cipher的初始化。
 */

class J1CryptoObjectCreator {

    private KeyGenerateImp keyGenerateImp;
    LocalSPUtil localSPUtil;
    public static J1CryptoObjectCreator cryptoObjectCreator = null;

    private J1CryptoObjectCreator (Context context){
        keyGenerateImp = new ISymmetricKeyGenerater();
        localSPUtil =new LocalSPUtil(context);
    };
    public static J1CryptoObjectCreator getInstance(Context context){

        if (cryptoObjectCreator == null){
            cryptoObjectCreator = new J1CryptoObjectCreator(context);
        }
        return cryptoObjectCreator;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public FingerprintManager.CryptoObject createCryptoObject(int purpose, String keyStoreAlias) throws Exception {
        Cipher cipher = initCipher(purpose,keyStoreAlias);
        FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
        return cryptoObject;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    private Cipher initCipher(int purpose,String keyStoreAlias) {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            if (purpose == PURPOSE_DECRYPT) {
                SecretKey key = keyGenerateImp.getSecretKey(keyStoreAlias);
                if (key == null) {
                    return null;
                }
                String iv = localSPUtil.getString(keyStoreAlias + IV_NAME);
                byte[] mIV = Base64.decode(iv, Base64.URL_SAFE);
                cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(mIV));
            } else {
                keyGenerateImp.generateKey(keyStoreAlias);
                SecretKey key = keyGenerateImp.getSecretKey(keyStoreAlias);
                cipher.init(Cipher.ENCRYPT_MODE, key);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return cipher;
    }


}
