package org.warmsheep.encoder.actor.processor;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOUtil;
import org.jpos.transaction.Context;
import org.warmsheep.encoder.actor.AbsActor;
import org.warmsheep.encoder.bean.A6CommandBean;
import org.warmsheep.encoder.constants.KeyConstants;
import org.warmsheep.encoder.constants.RespCmdType;
import org.warmsheep.encoder.ic.RespCodeIC;
import org.warmsheep.encoder.ic.TxnIC;
import org.warmsheep.encoder.security.util.EncryptUtil;
import org.warmsheep.encoder.security.util.OddEvenCheckUtil;


/**
 * A6指令处理器
 */
public class A6Processor extends AbsActor {


    @Override
    public int prepare(long id, Serializable serializable) {
        Context context = (Context) serializable;
        try {
            ISOMsg reqMsg = (ISOMsg) context.get(TxnIC.MSG_HSM);
            String header = reqMsg.getString(0);
            String commandType = reqMsg.getString(1);
            String requestData = reqMsg.getString(2);

            A6CommandBean a6CommandBean = A6CommandBean.build(header, commandType, requestData);
            //选择密钥
            String encryptKey = getEncryptKey(a6CommandBean.getKeyType());

            String zmkCipher = a6CommandBean.getZmkCipher();
            String zmkFlag = zmkCipher.substring(0, 1);
            if (zmkFlag.equalsIgnoreCase("X")) {
                zmkCipher = zmkCipher.substring(1);
            }
            //解密主密钥明文
            String zmkClearText = EncryptUtil.desDecryptToHex(zmkCipher, KeyConstants.ZMK_000);
            //明文进行奇偶校验
            zmkClearText = ISOUtil.hexString(OddEvenCheckUtil.parityOfOdd(ISOUtil.hex2byte(zmkClearText), 0));

            String keyClearText = null;
            //解密Key明文
            //单倍长
            if (a6CommandBean.getKeyFlag().equalsIgnoreCase("Z")) {
                keyClearText = EncryptUtil.desDecryptToHex(a6CommandBean.getKeyOnZmk(), zmkClearText);
            }
            //双倍长
            else if (a6CommandBean.getKeyFlag().equalsIgnoreCase("X")) {
                String keyOnZmk = a6CommandBean.getKeyOnZmk();
                if (keyOnZmk.substring(0, 1).equalsIgnoreCase("X")) {
                    keyOnZmk = keyOnZmk.substring(1);
                }
                keyClearText = EncryptUtil.desDecryptToHex(keyOnZmk, zmkClearText);
            }

            //明文进行奇偶校验
            keyClearText = ISOUtil.hexString(OddEvenCheckUtil.parityOfOdd(ISOUtil.hex2byte(keyClearText), 0));

            //用LMK加密
            String keyOnLmk = EncryptUtil.desEncryptHexString(keyClearText, encryptKey);
            //密钥值
            String checkValue = EncryptUtil.desEncryptHexString("0000000000000000", keyClearText);
            if (a6CommandBean.getKeyFlag().equalsIgnoreCase("X")) {
                keyOnLmk = "X" + keyOnLmk;
            }

            if (StringUtils.isNotBlank(keyOnLmk)) {
                context.put(TxnIC.RESULT_TYPE, RespCmdType.A7);
                context.put(TxnIC.RESULT_CODE, RespCodeIC.SUCCESS);
                context.put(TxnIC.RESULT_DATA, keyOnLmk.toUpperCase() + checkValue.toUpperCase());
            } else {
                context.put(TxnIC.RESULT_TYPE, RespCmdType.A7);
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
