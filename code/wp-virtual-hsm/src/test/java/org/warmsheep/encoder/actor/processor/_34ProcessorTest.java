package org.warmsheep.encoder.actor.processor;

import org.jpos.iso.ISOUtil;
import org.junit.Test;
import org.warmsheep.encoder.constants.KeyConstants;
import org.warmsheep.encoder.security.rsa.RSA;
import org.warmsheep.encoder.security.util.EncryptUtil;

import static org.junit.Assert.*;

/**
 * Created by qingyuan on 16-12-6.
 */
public class _34ProcessorTest {
    @Test
    public void prepare() throws Exception {

        RSA.generateKeyPair(512, "01");
        String privateKey = ISOUtil.hexString(RSA.getPrivateKeyBytes("01"));
        System.out.println(privateKey);
        privateKey = EncryptUtil.desEncryptHexString(privateKey, KeyConstants.RSA_034);
        String privateKeyLength = String.format("%04d",privateKey.length()/2);
        String publicKey = ISOUtil.hexString(RSA.getPublicKeyBytes("01"));
        System.out.println(privateKeyLength);
        System.out.println(privateKey);
        System.out.println(publicKey);
    }

}