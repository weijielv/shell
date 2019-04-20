package com.j1.j1finger.utils;

import javax.crypto.SecretKey;

/**
 * Created by weijie lv on 2019/4/18.in j1
 */

public interface KeyGenerateImp {

    void generateKey(String keystoreAlias);

    SecretKey getSecretKey(String keystoreAlias);

}



