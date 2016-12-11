package org.warmsheep.encoder.security.rsa;

import org.junit.Test;
import org.warmsheep.encoder.security.util.ByteUtil;

import static org.junit.Assert.*;

/**
 * Created by qingyuan on 16-12-6.
 */
public class RSACrtUtilTest {
    @Test
    public void prikey_crt_decrypt() throws Exception {

        int length = 64;
        byte[] publicKey = new byte[length];
        byte[] privateKey = new byte[length];

        RSACrtUtil.RSA_MODULUS_LEN = length;
        RSACrtUtil.generateKeyPair(publicKey, privateKey);

        System.out.println("SK："+ ByteUtil.toHexString(privateKey));
        System.out.println("PK："+ ByteUtil.toHexString(publicKey));
    }

}