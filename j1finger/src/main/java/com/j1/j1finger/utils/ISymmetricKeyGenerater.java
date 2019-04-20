package com.j1.j1finger.utils;

import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import static android.security.keystore.KeyProperties.PURPOSE_ENCRYPT;

/**
 * Created by weijie lv on 2019/4/18.in j1
 *
 * 生成加密key，保存到KeyStore中。
 */

public class ISymmetricKeyGenerater implements KeyGenerateImp {
    KeyStore mStore;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void generateKey(String keystoreAlias) {
        //这里使用AES + CBC + PADDING_PKCS7，并且需要用户验证方能取出，这里生成加密content的key
        if (TextUtils.isEmpty(keystoreAlias)){
            keystoreAlias = "j1keyStore";
        }
        try {
            final KeyGenerator generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            mStore = KeyStore.getInstance("AndroidKeyStore");
            mStore.load(null);
            final int purpose = KeyProperties.PURPOSE_DECRYPT | PURPOSE_ENCRYPT;
            final KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(keystoreAlias, purpose);
            builder.setUserAuthenticationRequired(true);
            builder.setBlockModes(KeyProperties.BLOCK_MODE_CBC);
            builder.setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7);
            generator.init(builder.build());
            generator.generateKey();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public SecretKey getSecretKey(String keystoreAlias) {
        initKeyStore();
        SecretKey key = null;
        try {
            key = (SecretKey) mStore.getKey(keystoreAlias, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return key;
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
//    @RequiresApi(api = Build.VERSION_CODES.M)
//    private void generateKey() throws NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {
//        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_EC,
//                "AndroidKeyStore");
//        keyPairGenerator.initialize(
//                new KeyGenParameterSpec.Builder(KEY_NAME,
//                        KeyProperties.PURPOSE_SIGN)
//                        .setDigests(KeyProperties.DIGEST_SHA256)
//                        .setAlgorithmParameterSpec(new ECGenParameterSpec("secp256r1"))
//                        .setUserAuthenticationRequired(true)
//                        .build());
//        keyPairGenerator.generateKeyPair();
//    }
    private void selectKeys(String alias) throws CertificateException, NoSuchAlgorithmException, IOException, KeyStoreException, UnrecoverableKeyException {


        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        PublicKey publicKey =keyStore.getCertificate(alias).getPublicKey();

        //KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        //keyStore.load(null);
        PrivateKey key = (PrivateKey) keyStore.getKey(alias, null);
        //Log.e(TAG, "publickey = " + new String(publicKey.getEncoded()));
        //Log.e(TAG, "privatekey = " + new String(key.getEncoded(), "UTF-8"));
    }
}
