package org.warmsheep.encoder.actor.processor;

import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOUtil;
import org.jpos.transaction.Context;
import org.warmsheep.encoder.actor.AbsActor;
import org.warmsheep.encoder.bean.A4CommandBean;
import org.warmsheep.encoder.constants.KeyConstants;
import org.warmsheep.encoder.constants.RespCmdType;
import org.warmsheep.encoder.ic.RespCodeIC;
import org.warmsheep.encoder.ic.TxnIC;
import org.warmsheep.encoder.security.util.EncryptUtil;
import org.warmsheep.encoder.security.util.OddEvenCheckUtil;

import java.io.Serializable;

/**
 * Created by qingyuan on 16-12-5.
 */
public class A4Processor extends AbsActor {
    @Override
    public int prepare(long id, Serializable serializable) {
        Context context = (Context) serializable;
        try {
            ISOMsg reqMsg = (ISOMsg) context.get(TxnIC.MSG_HSM);
            String header = reqMsg.getString(0);
            String commandType = reqMsg.getString(1);
            String requestData = reqMsg.getString(2);

            A4CommandBean a4CommandBean = A4CommandBean.build(header, commandType, requestData);

            String encryptKey = getEncryptKey(a4CommandBean.getKeyType());
            String key =null, keyOnLmk = null;
            int factorNum = Integer.parseInt(a4CommandBean.getFactorNum());
            if (factorNum == 2){
                String factor1 = EncryptUtil.desDecryptToHex(a4CommandBean.getFactors()[0], KeyConstants.ZMK_000);
                String factor2 = EncryptUtil.desDecryptToHex(a4CommandBean.getFactors()[1], KeyConstants.ZMK_000);

                key = EncryptUtil.xor(factor1,factor2);
            } else if (factorNum == 3) {
                String factor1 = EncryptUtil.desDecryptToHex(a4CommandBean.getFactors()[0], KeyConstants.ZMK_000);
                String factor2 = EncryptUtil.desDecryptToHex(a4CommandBean.getFactors()[1], KeyConstants.ZMK_000);
                String factor3 = EncryptUtil.desDecryptToHex(a4CommandBean.getFactors()[1], KeyConstants.ZMK_000);

                String tmp = EncryptUtil.xor(factor1,factor2);
                key = EncryptUtil.xor(tmp,factor3);
            } else {
                context.put(TxnIC.RESULT_TYPE, RespCmdType.A5);
                context.put(TxnIC.RESULT_CODE, RespCodeIC.INVALID_FACTOR_NUM);
            }

            if (key != null){
                keyOnLmk = EncryptUtil.desEncryptHexString(key, encryptKey);
            }

            if (keyOnLmk != null) {
                //进行奇偶校验
                byte[] keyBytes = OddEvenCheckUtil.parityOfOdd(ISOUtil.hex2byte(key), 0);
                //产生校验值
                String checkValue = EncryptUtil.desEncryptHexString("0000000000000000", ISOUtil.hexString(keyBytes));

                if (a4CommandBean.getLmkKeyFlag().equalsIgnoreCase("X")) {
                    keyOnLmk = "X"+keyOnLmk;
                }

                context.put(TxnIC.RESULT_TYPE, RespCmdType.A5);
                context.put(TxnIC.RESULT_CODE, RespCodeIC.SUCCESS);
                context.put(TxnIC.RESULT_DATA, keyOnLmk.toUpperCase() + checkValue.toUpperCase());
            } else {
                context.put(TxnIC.RESULT_TYPE, RespCmdType.A5);
                context.put(TxnIC.RESULT_CODE, RespCodeIC.FORMAT_ERROR);
            }

            return PREPARED | NO_JOIN;
        } catch (Exception e){
            logger.error("A4指令处理出现异常", e);
            context.put(TxnIC.RESULT_CODE, RespCodeIC.OTHER_ERROR);
            return ABORTED | NO_JOIN;
        }
    }
}
