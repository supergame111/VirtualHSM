package org.warmsheep.encoder.security.rsa;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.Cipher;

/**
 * Created by qingyuan on 16-12-6.
 */
public class RSACrtUtil {

    public static int RSA_MODULUS_LEN = 128;
    private static int RSA_P_LEN = RSA_MODULUS_LEN / 2;
    private static int RSA_Q_LEN = RSA_MODULUS_LEN / 2;
    public static int publicExponent = 65537;
    public static final String KEY_ALGORITHM_MODE_PADDING = "RSA/ECB/NoPadding"; //不填充
    public static final String KEY_ALGORITHM = "RSA"; //不填充

    /**
     * prikey_crt_decrypt 使用PQ的RSA私钥解密
     * 私钥格式前半部分是P,后半部分是Q
     */
    public static byte[] prikey_crt_decrypt(byte[] data, byte[] prikey) throws Exception {


        byte[] buf_p = new byte[RSA_P_LEN];
        byte[] buf_q = new byte[RSA_Q_LEN];
        //buf_p[0] = (byte)0x00;
        //buf_q[0] = (byte)0x00;
        System.arraycopy(prikey, 0, buf_p, 0, RSA_P_LEN);
        System.arraycopy(prikey, RSA_P_LEN, buf_q, 0, RSA_Q_LEN);
        //
        /**
         *  1.p,q计算n
         * */
        BigInteger p = new BigInteger(1, buf_p);
        BigInteger q = new BigInteger(1, buf_q);
        BigInteger n = p.multiply(q); //n = p * q
        /**
         *  2. 计算d = (p-1) * (q-1) mod e
         * */
        BigInteger p1 = p.subtract(BigInteger.valueOf(1));
        BigInteger q1 = q.subtract(BigInteger.valueOf(1));
        BigInteger h = p1.multiply(q1);// h = (p-1) * (q-1)
        BigInteger e = BigInteger.valueOf(publicExponent);
        //BigInteger d = h.mod(e);
        BigInteger d = e.modInverse(h);

        /**
         *  3. 创建 RSA私钥
         * */
        RSAPrivateKeySpec keyspec = new RSAPrivateKeySpec(n, d);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateKey = keyFactory.generatePrivate(keyspec);
        /**
         *  4. 数据解密
         * */
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM_MODE_PADDING);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        /**
         *  5. 返回结果
         * */
        return cipher.doFinal(data);
    }

    /**
     * pubkey_encrypt 公钥加密
     * 密钥是N
     */
    public static byte[] pubkey_encrypt(byte[] data, byte[] pubkey) throws Exception {

        /**
         *  1.初始化大数模n和公钥指数e
         * */
        byte[] pubkey_buf = new byte[RSA_MODULUS_LEN + 1];//多一字节符号位
        pubkey_buf[0] = (byte) 0x00;
        System.arraycopy(pubkey, 0, pubkey_buf, 1, RSA_MODULUS_LEN);
        //
        BigInteger e = BigInteger.valueOf(publicExponent);
        BigInteger n = new BigInteger(pubkey_buf);
        /**
         *  2.创建RSA公钥
         * */
        //
        RSAPublicKeySpec keyspec = new RSAPublicKeySpec(n, e);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicKey = keyFactory.generatePublic(keyspec);
        /**
         *  3.数据加密
         * */
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM_MODE_PADDING);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        /**
         *  5. 返回结果
         * */
        return cipher.doFinal(data);
    }

    public static void generateKeyPair(byte[] pubkey, byte[] prikey) throws Exception {

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGenerator.initialize(RSA_MODULUS_LEN * 8);

        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateCrtKey privateKey = (RSAPrivateCrtKey) keyPair.getPrivate();
        //
        BigInteger n = publicKey.getModulus();
        BigInteger p = privateKey.getPrimeP();
        BigInteger q = privateKey.getPrimeQ();
        /**
         *  BigInteger 里有一个bit的符号位,所以直接用toByteArray会包含符号位,
         *  在c的代码里没符号位,所以1024bit的n,java里BigInteger是1025bit长
         *  直接拷贝128byte出来,正数第一个字节是是0,后面会丢掉最后一字节
         * */
        System.arraycopy(n.toByteArray(), 1, pubkey, 0, 128);
        System.arraycopy(p.toByteArray(), 1, prikey, 0, 64);
        System.arraycopy(q.toByteArray(), 1, prikey, 64, 64);
        //
    }
}
