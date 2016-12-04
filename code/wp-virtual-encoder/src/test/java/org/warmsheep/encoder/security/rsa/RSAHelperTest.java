package org.warmsheep.encoder.security.rsa;

import org.junit.Test;
import org.warmsheep.encoder.constants.KeyConstants;
import org.warmsheep.encoder.security.mac.impl.ANSIX919;
import org.warmsheep.encoder.security.util.ByteUtil;
import org.warmsheep.encoder.security.util.EncryptUtil;

import static org.junit.Assert.*;

/**
 * Created by qingyuan on 16/12/4.
 */
public class RSAHelperTest {
    @Test
    public void sign() throws Exception {

        String data = "qingyuan@ftsafe.com";
        RSA.generateKeyPair(1024,"01");
        System.out.println("私钥BASE64："+ (RSA.getPrivateKey("01")));
        System.out.println("公钥BASE64："+ (RSA.getPublicKey("01")));
        System.out.println("私钥："+ ByteUtil.bytesToHexString(EncryptUtil.decryptBASE64(RSA.getPrivateKey("01"))));
        System.out.println("公钥："+ ByteUtil.bytesToHexString(EncryptUtil.decryptBASE64(RSA.getPublicKey("01"))));
        String signature = RSAHelper.sign(data.getBytes(),RSA.getPrivateKey("01"));
        System.out.println("原始数据："+data);
        System.out.println("私钥签名结果："+signature);
        System.out.println("公钥校验结果："+RSAHelper.verify(data.getBytes(),RSA.getPublicKey("01"),signature));

        String encTAK = "01E763CEE23841B6B014A98CDB9E00CD";
        String macData = "qingyuan";
        String takClearText = EncryptUtil.desDecryptToHex(encTAK, KeyConstants.TAK_003);
        ANSIX919 ansix919 = new ANSIX919();
        byte[] tak = ByteUtil.hexStringToByte(takClearText);
        byte[] src = macData.getBytes();
        byte[] mac = ansix919.getMac(src,tak);
        System.out.println("TAK密文："+encTAK);
        System.out.println("TAK明文："+takClearText);
        System.out.println("MAC数据："+macData);
        System.out.println("MAC："+ByteUtil.bytesToHexString(mac));

    }


}