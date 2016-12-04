package org.warmsheep.encoder.actor.processor;

import java.io.Serializable;

import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOUtil;
import org.jpos.transaction.Context;
import org.warmsheep.encoder.actor.AbsActor;
import org.warmsheep.encoder.bean.MSCommandBean;
import org.warmsheep.encoder.constants.KeyConstants;
import org.warmsheep.encoder.constants.RespCmdType;
import org.warmsheep.encoder.enums.KeyLengthType;
import org.warmsheep.encoder.enums.KeyType;
import org.warmsheep.encoder.ic.RespCodeIC;
import org.warmsheep.encoder.ic.TxnIC;
import org.warmsheep.encoder.security.mac.impl.ANSIX919;
import org.warmsheep.encoder.security.mac.impl.ANSIX99;
import org.warmsheep.encoder.security.util.EncryptUtil;
import org.warmsheep.encoder.security.util.OddEvenCheckUtil;

/**
 * MS指令处理器
 * 
 */
public class MSProcessor extends AbsActor {

	
	@Override
	public int prepare(long id, Serializable serializable) {
		Context context = (Context) serializable;
		try {
			ISOMsg reqMsg = (ISOMsg) context.get(TxnIC.MSG_HSM);
			String header = reqMsg.getString(0);
			String commandType = reqMsg.getString(1);
			String requestData = reqMsg.getString(2);
			
			MSCommandBean msCommandBean = MSCommandBean.build(header, commandType, requestData);
			
			byte[] macBytes = null;
			String deKey = null;
			if(msCommandBean.getKeyType().equals(KeyType.ZAK.getKey())){
				deKey = KeyConstants.ZAK_008;
			} else {
				deKey = KeyConstants.TAK_003;
			}
			
			//双倍长密钥算法
			if(msCommandBean.getKeyLengthType().equals(KeyLengthType.DOUBLE_LENGTH.getKey())){
				ANSIX919 ansix919 = new ANSIX919();
				String encryptKeyValue = null;
				//密钥第一位为X
				if(msCommandBean.getKeyValue().substring(0,1).equalsIgnoreCase("X")){
					encryptKeyValue = msCommandBean.getKeyValue().substring(1);
				} 
				//密钥第一位不为X
				else {
					encryptKeyValue = msCommandBean.getKeyValue();
				}
				//解密密钥
				String clearKey = EncryptUtil.desDecryptToHex(encryptKeyValue, deKey);
				//明文进行奇偶校验
				clearKey = ISOUtil.hexString(OddEvenCheckUtil.parityOfOdd(ISOUtil.hex2byte(clearKey), 0));
				
				//计算MAC
				macBytes = ansix919.getMac(ISOUtil.hex2byte(msCommandBean.getEncryptDataValue()), ISOUtil.hex2byte(clearKey));
			} 
			//单倍长密钥算法
			else if(msCommandBean.getKeyLengthType().equals(KeyLengthType.SINGLE_LENGTH.getKey())){
				ANSIX99 ansix99 = new ANSIX99();
				String clearKey = EncryptUtil.desDecryptToHex(msCommandBean.getKeyValue(), deKey);
				//明文进行奇偶校验
				clearKey = ISOUtil.hexString(OddEvenCheckUtil.parityOfOdd(ISOUtil.hex2byte(clearKey), 0));
				macBytes = ansix99.getMac(ISOUtil.hex2byte(msCommandBean.getEncryptDataValue()), ISOUtil.hex2byte(clearKey));
			}
			
			if(macBytes != null){
				context.put(TxnIC.RESULT_TYPE, RespCmdType.MT);
				context.put(TxnIC.RESULT_CODE, RespCodeIC.SUCCESS);
				context.put(TxnIC.RESULT_DATA, ISOUtil.hexString(macBytes));
			} else {
				context.put(TxnIC.RESULT_TYPE, RespCmdType.MT);
				//TODO待实现
				context.put(TxnIC.RESULT_CODE, RespCodeIC.FORMAT_ERROR);
			}
			
			return PREPARED | NO_JOIN;
		} catch (Exception e) {
			logger.error("MS指令处理出现异常", e);
			context.put(TxnIC.RESULT_CODE, RespCodeIC.OTHER_ERROR);
			return ABORTED | NO_JOIN;
		}
	}
	
	
}
