package org.warmsheep.encoder.security.util;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.digest.DigestUtils;
import org.jpos.iso.ISOUtil;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * DES加密工具类
 * @author Warmsheep
 *
 */
public class EncryptUtil {
	
	private static final String model = "DESede/ECB/NoPadding";

	/**
	 * DES解密
	 * @param message
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String desDecrypt(String message, String key) throws Exception {
		try {
			byte[] keyBytes = null;
			if(key.length() == 16){
				keyBytes = newInstance8Key(ByteUtil.convertHexString(key));
			} else if(key.length() == 32){
				keyBytes = newInstance16Key(ByteUtil.convertHexString(key));
			} else if(key.length() == 48){
				keyBytes = newInstance24Key(ByteUtil.convertHexString(key));
			}
			SecretKey deskey = new SecretKeySpec(keyBytes, "DESede");
			Cipher c1 = Cipher.getInstance(model);
			c1.init(2, deskey);
			byte[] retByte = c1.doFinal(ByteUtil.convertHexString(message));
			
			return new String(retByte);
		} catch (Exception e) {
			System.err.println(e);
		}
		return null;
	}
	
	/**
	 * DES解密
	 * @param message
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String desDecryptToHex(String message, String key) throws Exception {
		try {
			byte[] keyBytes = null;
			if(key.length() == 16){
				keyBytes = newInstance8Key(ByteUtil.convertHexString(key));
			} else if(key.length() == 32){
				keyBytes = newInstance16Key(ByteUtil.convertHexString(key));
			} else if(key.length() == 48){
				keyBytes = newInstance24Key(ByteUtil.convertHexString(key));
			}
			SecretKey deskey = new SecretKeySpec(keyBytes, "DESede");
			Cipher c1 = Cipher.getInstance(model);
			c1.init(2, deskey);
			byte[] retByte = c1.doFinal(ByteUtil.convertHexString(message));
			
			return ByteUtil.toHexString(retByte);
		} catch (Exception e) {
			System.err.println(e);
		}
		return null;
	}

	
	/**
	 * DES加密
	 * @param message
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String desEncrypt(String message, String key) throws Exception {
		byte[] keyBytes = null;
		if(key.length() == 16){
			keyBytes = newInstance8Key(ByteUtil.convertHexString(key));
		} else if(key.length() == 32){
			keyBytes = newInstance16Key(ByteUtil.convertHexString(key));
		} else if(key.length() == 48){
			keyBytes = newInstance24Key(ByteUtil.convertHexString(key));
		}
		
		SecretKey deskey = new SecretKeySpec(keyBytes, "DESede");

		Cipher cipher = Cipher.getInstance(model);
		cipher.init(1, deskey);
		return ISOUtil.hexString(cipher.doFinal(message.getBytes("UTF-8")));
	}
	
	public static String desEncryptHexString(String message,String key) throws Exception {
		byte[] keyBytes = null;
		if(key.length() == 16){
			keyBytes = newInstance8Key(ByteUtil.convertHexString(key));
		} else if(key.length() == 32){
			keyBytes = newInstance16Key(ByteUtil.convertHexString(key));
		} else if(key.length() == 48){
			keyBytes = newInstance24Key(ByteUtil.convertHexString(key));
		}
		
		SecretKey deskey = new SecretKeySpec(keyBytes, "DESede");

		Cipher cipher = Cipher.getInstance(model);
		cipher.init(1, deskey);
		return ISOUtil.hexString(cipher.doFinal(ISOUtil.hex2byte(message)));
	}
	
	/**
	 * sha1Hex加密
	 * @param message
	 * @return
	 * @throws Exception
	 */
	public static String sha1HexEncrypt(String message) throws Exception {
		return DigestUtils.sha1Hex(message);
	}

	/**
	 * 密码加密，先SHA加密，然后使用DES加密
	 * @param password
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String passwordEncrypt(String password,String key) throws Exception{
		password = DigestUtils.sha1Hex(password);
		password = desEncrypt(password, key);
		return password;
	}

	/*** 
	* MD5加码 生成32位md5码 
	*/
	public static String md5Encrypt(String message) {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();
			return "";
		}
		byte[] md5Bytes = md5.digest(message.getBytes());
		String hexValue = ByteUtil.toHexString(md5Bytes);
		return hexValue;

	}

	
	private static byte[] newInstance24Key(byte[] key) {
		if ((key != null) && (key.length == 24)) {
			return key;
		}
		System.err.println("密钥长度有误,期望值[24]");
		return null;
	}

	private static byte[] newInstance16Key(byte[] key) {
		if ((key != null) && (key.length == 16)) {
			byte[] b = new byte[24];
			System.arraycopy(key, 0, b, 0, 16);
			System.arraycopy(key, 0, b, 16, 8);
			key = (byte[]) null;
			return b;
		}
		System.err.println("密钥长度有误,期望值[16]");
		return null;
	}

	private static byte[] newInstance8Key(byte[] key) {
		if ((key != null) && (key.length == 8)) {
			byte[] b = new byte[24];
			System.arraycopy(key, 0, b, 0, 8);
			System.arraycopy(key, 0, b, 8, 8);
			System.arraycopy(key, 0, b, 16, 8);
			key = (byte[]) null;
			return b;
		}
		System.err.println("密钥长度有误,期望值[8]");
		return null;
	}

	//解码返回byte
	public static byte[] decryptBASE64(String key) throws Exception {
		return (new BASE64Decoder()).decodeBuffer(key);
	}

	//编码返回字符串
	public static String encryptBASE64(byte[] key) throws Exception {
		return (new BASE64Encoder()).encodeBuffer(key);
	}

	public static String xor(String p1, String p2) {
		byte[] b1 = ByteUtil.convertHexString(p1), b2 = ByteUtil.convertHexString(p2);
		if (b1.length != b2.length){
			System.err.println("参与异或的数据长度必须一致，参数一的长度："+b1.length+"参数二的长度："+b2.length);
			return null;
		}
		byte[] buf = new byte[b1.length];
		for (int i = 0; i< b1.length; i++){
			buf[i] = (byte) (b1[i]^b2[i]);
		}

		return ByteUtil.bytesToHexString(buf);
	}

	public static void main(String[] args) throws Exception {
		// 加密密码测试DEMO
//		System.out.println(md5Encrypt("湖南"));
		String key = "0123456789ABCDEFFEDCBA9876543210";
		String password = "11111111";
		System.out.println("密钥:"+key+"长度:"+String.format("%04x",key.length()));
		System.out.println("加密前的明文:" + password);
		
		String desText = "";
		try {
			String sha1Text = sha1HexEncrypt(password);
			System.out.println("使用sha1加密后的密文:" + sha1Text);
			desText = desEncrypt(sha1Text, key);
			System.out.println("使用des加密后的密文:" + desText);
			System.out.println("使用des解密后的明文:" + desDecrypt(desText, key));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
