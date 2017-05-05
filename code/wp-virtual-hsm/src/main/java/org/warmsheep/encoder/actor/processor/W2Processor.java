package org.warmsheep.encoder.actor.processor;

import java.io.Serializable;

import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOUtil;
import org.jpos.transaction.Context;
import org.warmsheep.encoder.actor.AbsActor;
import org.warmsheep.encoder.bean.W2CommandBean;
import org.warmsheep.encoder.constants.RespCmdType;
import org.warmsheep.encoder.ic.RespCodeIC;
import org.warmsheep.encoder.ic.TxnIC;
import org.warmsheep.encoder.security.des.impl.DESede;

public class W2Processor extends AbsActor {

    private static final String LMK_109 = "ArCv0lrEPPmGMa47qgHNh8RadzSUY3w9";
    private static final String LMK_209 = "ZJuEp44l3wiFZFDh5PASLUgGE9QNi4Um";

	@Override
	public int prepare(long id, Serializable serializable) {
        Context context = (Context) serializable;
        try {
            ISOMsg reqMsg = (ISOMsg) context.get(TxnIC.MSG_HSM);
            String header = reqMsg.getString(0);
            String commandType = reqMsg.getString(1);
            String requestData = reqMsg.getString(2);

            W2CommandBean commandBean = W2CommandBean.build(header, commandType, requestData);
            String encMode = commandBean.getEncModeFlag();
            String solutionId = commandBean.getSolutionId();
            String rootKeyType = commandBean.getRootKeyType();
            String rootKey = commandBean.getRootKey().substring(1);
            String data = commandBean.getData();

            // LMK 解密
            if (rootKeyType.equals("109")) {
                DESede rootKeydes = DESede.newInstance16(ISOUtil.hex2byte(LMK_109));
                rootKey = ISOUtil.hexString(rootKeydes.decrypt(ISOUtil.hex2byte(rootKey)));
            } else if (rootKeyType.equals("209")){
                DESede rootKeydes = DESede.newInstance16(ISOUtil.hex2byte(LMK_209));
                rootKey = ISOUtil.hexString(rootKeydes.decrypt(ISOUtil.hex2byte(rootKey)));
            } else{

            }
            
            context.put(TxnIC.RESULT_TYPE, RespCmdType.W3);
            String result = "";
            if (encMode.equals("1")){ // 3DES-CBC
                
            } else if(encMode.equals("2")) { // DES-CBC
            	
            } else if (encMode.equals("3")) { // DES-ECB
            	
            } else if (encMode.equals("4")) { // 3DES-ECB
            	DESede desEde = DESede.newInstance16(ISOUtil.hex2byte(rootKey));
            	if(solutionId.equals("0")) // 加密
            		result = ISOUtil.hexString(desEde.encrypt(ISOUtil.hex2byte(data)));
            	else if (solutionId.equals("1")) // 解密
            		result = ISOUtil.hexString(desEde.decrypt(ISOUtil.hex2byte(data)));
            	else {
            		context.put(TxnIC.RESULT_CODE, RespCodeIC.UNDEFINED_SOLUTION_ID);
            	}
            	
            } else if (encMode.equals("0")) {
            	
            } else {
                context.put(TxnIC.RESULT_CODE, RespCodeIC.MODE_ERROR);
            }
            String resultLen = String.format("%03d", result.length()/2);
            context.put(TxnIC.RESULT_DATA, resultLen+result);
            return PREPARED | NO_JOIN;
        } catch (Exception e){
            logger.error("W2指令处理出现异常", e);
            context.put(TxnIC.RESULT_CODE, RespCodeIC.OTHER_ERROR);
            return ABORTED | NO_JOIN;
        }

        }

}
