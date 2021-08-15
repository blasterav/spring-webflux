package com.phoosop.reactive.util;

import java.security.KeyPair;
import java.security.KeyPairGenerator;

public class CryptoUtils {

    public static KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        return kpg.generateKeyPair();
    }
}
