package com.j1.j1finger.utils;

import android.hardware.biometrics.BiometricPrompt;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;

import com.j1.j1finger.J1KeyValue;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.ECGenParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import static android.security.keystore.KeyProperties.PURPOSE_ENCRYPT;

/**
 * Created by weijie lv on 2019/4/18.in j1
 *
 * 生成加密key，保存到KeyStore中。
 */

public class IShaKeyGenerater implements KeyGenerateImp {
    KeyStore mStore;
//    @RequiresApi(api = Build.VERSION_CODES.M)
//    @Override
//    public void generateKey(String keystoreAlias) {
//        //这里使用AES + CBC + PADDING_PKCS7，并且需要用户验证方能取出，这里生成加密content的key
//        if (TextUtils.isEmpty(keystoreAlias)){
//            keystoreAlias = "j1keyStore";
//        }
//        try {
//            final KeyGenerator generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
//            mStore = KeyStore.getInstance("AndroidKeyStore");
//            mStore.load(null);
//            final int purpose = KeyProperties.PURPOSE_DECRYPT | PURPOSE_ENCRYPT;
//            final KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(keystoreAlias, purpose);
//            builder.setUserAuthenticationRequired(true);
//            builder.setBlockModes(KeyProperties.BLOCK_MODE_CBC);
//            builder.setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7);
//            generator.init(builder.build());
//            generator.generateKey();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public SecretKey getSecretKey(String keystoreAlias) {
        initKeyStore();
        SecretKey key = null;
        try {
            key = (SecretKey) mStore.getKey(keystoreAlias, null);
            //mStore.getCertificate(keystoreAlias).getPublicKey();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return key;
    }

    public PublicKey getPublicKey(String keystoreAlias) {
        initKeyStore();
        PublicKey publicKey = null;
        try {
            mStore.load(null);
            publicKey =mStore.getCertificate(keystoreAlias).getPublicKey();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return publicKey;
    }

    public Signature getSignature() {
        Signature signature = null;
        try {
             signature = Signature.getInstance(KeyProperties.KEY_ALGORITHM_EC, "AndroidKeyStore");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return signature;
    }
    public Cipher getCipher() {
       return null;
    }

    void initKeyStore(){
        try {
            mStore = KeyStore.getInstance("AndroidKeyStore");
            mStore.load(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成公钥和私钥
     *
     * @throws NoSuchProviderException
     * @throws NoSuchAlgorithmException
     * @throws InvalidAlgorithmParameterException
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void generateKey(String keystoreAlias) {
        KeyPairGenerator keyPairGenerator = null;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_EC,
                    keystoreAlias);

        keyPairGenerator.initialize(
                new KeyGenParameterSpec.Builder(keystoreAlias,
                        KeyProperties.PURPOSE_SIGN)
                        .setDigests(KeyProperties.DIGEST_SHA256)
                        .setAlgorithmParameterSpec(new ECGenParameterSpec("secp256r1"))
                        .setUserAuthenticationRequired(true)
                        .build());
        keyPairGenerator.generateKeyPair();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 加密或者签名。
     *
     * @param result1 sdk 版本小于28大于23，需要传入
     * @param result2 sdk版本大于28，传入这个参数。
     * @param kv
     */
    public boolean encryption(@Nullable FingerprintManager.AuthenticationResult result1, @Nullable BiometricPrompt.AuthenticationResult result2, J1KeyValue<String, String> kv) {
        return false;
    }

    /**
     * 解密或者验证签名。
     *
     * @param result1 sdk 版本小于28大于23，需要传入
     * @param result2 sdk版本大于28，传入这个参数。
     * @param kv
     */
    public boolean decode(@Nullable FingerprintManager.AuthenticationResult result1, @Nullable BiometricPrompt.AuthenticationResult result2, J1KeyValue<String, String> kv) {
        return false;
    }
}
