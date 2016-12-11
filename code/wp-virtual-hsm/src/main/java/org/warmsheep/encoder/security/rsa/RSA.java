package org.warmsheep.encoder.security.rsa;

import org.warmsheep.encoder.security.util.EncryptUtil;

import java.security.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by qingyuan on 16/12/3.
 */
public class RSA {

    private static Map<String,KeyPair> keyStore = new HashMap<>();

    public static final String KEY_ALGORITHM = "RSA";

    public static String getPublicKey(String index) throws Exception {
        //获得map中的公钥对象 转为key对象
        Key key = keyStore.get(index).getPublic();
        //编码返回字符串
        return EncryptUtil.encryptBASE64(key.getEncoded());
    }

    public static byte[] getPublicKeyBytes(String index) {
        return keyStore.get(index).getPublic().getEncoded();
    }

    public static String getPrivateKey(String index) throws Exception {
        //获得map中的公钥对象 转为key对象
        Key key = keyStore.get(index).getPrivate();
        //编码返回字符串
        return EncryptUtil.encryptBASE64(key.getEncoded());
    }

    public static byte[] getPrivateKeyBytes(String index) {
        return keyStore.get(index).getPrivate().getEncoded();
    }

    public static KeyPair generateKeyPair(int length, String index) throws NoSuchAlgorithmException {

        //获得对象 KeyPairGenerator 参数
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGen.initialize(length);
        //通过对象 KeyPairGenerator 获取对象KeyPair
        KeyPair keyPair = keyPairGen.generateKeyPair();

        //公私钥对象存入map中
        keyStore.put(index,keyPair);

        return keyPair;
    }


}
