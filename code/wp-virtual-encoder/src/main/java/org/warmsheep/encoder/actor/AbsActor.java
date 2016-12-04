package org.warmsheep.encoder.actor;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.transaction.TransactionParticipant;
import org.warmsheep.encoder.constants.KeyConstants;
import org.warmsheep.encoder.ic.LogIC;
/**
 * 业务处理器、业务验证器和选择器的抽象类，
 * 用于实现空的commit和abort方法
 */
public abstract class AbsActor implements TransactionParticipant,Configurable {
	
	/**
	 * 日志的记录类
	 */
	protected static Log logger = LogFactory.getLog(LogIC.HSM);
	
	protected Configuration cfg;
	
	protected String downloadFileDirectory;
	protected String reportPeriod;

	public String getEncryptKey(String keyType){
		//选择密钥
		String encryptKey = null;
		switch (keyType) {
			case "000":    //ZMK/KEK
				encryptKey = KeyConstants.ZMK_000;
				break;
			case "001":    //ZPK
				encryptKey = KeyConstants.ZPK_001;
				break;
			case "002":    //PVK/TPK/TMK
				encryptKey = KeyConstants.TPK_TMK_PVK_002;
				break;
			case "003":    //TAK
				encryptKey = KeyConstants.TAK_003;
				break;
			case "007":    //edk
				encryptKey = KeyConstants.EDK_007;
				break;
			case "008"://ZAK
				encryptKey = KeyConstants.ZAK_008;
				break;
			case "00A":    //ZEK/DEK
				break;
			case "00C":
				break;
			case "100":
				break;
			case "109":
				break;
			case "209":
				break;
			case "309":
				break;
			case "402":
				break;
			case "509":
				break;
			default:
				break;
		}
		return encryptKey;
	}
	/**
	 * 处理方法，由子类来实现
	 */
	@Override
	public abstract int prepare(long id, Serializable context);

	/**
	 * 验证通过时，调用该方法
	 */
	@Override
	public void commit(long id, Serializable context) {
	}

	/**
	 * 验证拒绝时，调用该方法
	 */
	@Override
	public void abort(long id, Serializable context) {
	}

	@Override
	public void setConfiguration(Configuration cfg)
			throws ConfigurationException {
		this.cfg = cfg;
	}
	
	/**
	 * 计算校验值
	 * @param msg
	 * @return
	 * @throws ISOException
	 */
	protected byte[] fecth(ISOMsg msg) throws ISOException {
		byte[] src = msg.pack();
		int newByteLen = src.length;
		//有第5域（校验值），则将长度减1
		if (msg.hasField(5))
			newByteLen = newByteLen - 1;
		byte[] newByte = new byte[newByteLen];
		//数组拷贝
		System.arraycopy(src, 0, newByte, 0, newByte.length);
		return this.fetch(newByte);
	}
	
	/**
	 * 计算校验值
	 * @param b 
	 * @return
	 */
	private byte[] fetch(byte[] b) {
		byte b0 = 0x00;
		for (int i = 0; i < b.length; i++) {
			b0 = (byte) (b0 ^ b[i]);
		}
		return new byte[] { b0 };
	}

}
