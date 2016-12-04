package org.warmsheep.encoder.actor.processor;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOUtil;
import org.jpos.transaction.Context;
import org.warmsheep.encoder.actor.AbsActor;
import org.warmsheep.encoder.bean.A0CommandBean;
import org.warmsheep.encoder.constants.KeyConstants;
import org.warmsheep.encoder.constants.RespCmdType;
import org.warmsheep.encoder.ic.RespCodeIC;
import org.warmsheep.encoder.ic.TxnIC;
import org.warmsheep.encoder.security.util.EncryptUtil;
import org.warmsheep.encoder.security.util.OddEventCheckUtil;
import org.warmsheep.encoder.util.UUIDUitl;


/**
 * A0指令处理器
 */
public class A0Processor extends AbsActor {


    @Override
    public int prepare(long id, Serializable serializable) {
        Context context = (Context) serializable;
        try {
            ISOMsg reqMsg = (ISOMsg) context.get(TxnIC.MSG_HSM);
            String header = reqMsg.getString(0);
            String commandType = reqMsg.getString(1);
            String requestData = reqMsg.getString(2);

            A0CommandBean a0CommandBean = A0CommandBean.build(header, commandType, requestData);

            byte[] randomKeyBytes = null;

            if (a0CommandBean.getLmkKeyFlag().equals("X")) {
                randomKeyBytes = UUIDUitl.generateString(16).getBytes();
            } else if (a0CommandBean.getLmkKeyFlag().equals("Z")) {
                randomKeyBytes = UUIDUitl.generateString(8).getBytes();
            }

            if (!a0CommandBean.getLmkKeyFlag().equals(a0CommandBean.getZmkKeyFlag())) {
                throw new Exception("LMK/ZMK KEY FLAG NOT EQUALS!");
            }

            //进行奇偶校验
            randomKeyBytes = OddEventCheckUtil.parityOfOdd(randomKeyBytes, 0);
            //产生校验值
            String checkValue = EncryptUtil.desEncryptHexString("0000000000000000", ISOUtil.hexString(randomKeyBytes));
            String encryptKey = getEncryptKey(a0CommandBean.getKeyType());

            String randomKeyOnLmk = EncryptUtil.desEncryptHexString(ISOUtil.hexString(randomKeyBytes), encryptKey);
            if (a0CommandBean.getLmkKeyFlag().equalsIgnoreCase("X")) {
                randomKeyOnLmk = "X" + randomKeyOnLmk;
            }
            String randomKeyOnZmk = "";
            if (a0CommandBean.getGenerateMode().equals("1")) {
                String zmkCipher = a0CommandBean.getZmkCipher();
                if (zmkCipher.substring(0, 1).equalsIgnoreCase("X")) {
                    zmkCipher = zmkCipher.substring(1);
                }
                String zmkClearText = EncryptUtil.desDecryptToHex(zmkCipher, KeyConstants.ZMK_000);
                //明文进行奇偶校验
                zmkClearText = ISOUtil.hexString(OddEventCheckUtil.parityOfOdd(ISOUtil.hex2byte(zmkClearText), 0));


                randomKeyOnZmk = EncryptUtil.desEncryptHexString(ISOUtil.hexString(randomKeyBytes), zmkClearText);
                if (a0CommandBean.getZmkKeyFlag().equalsIgnoreCase("X")) {
                    randomKeyOnZmk = "X" + randomKeyOnZmk;
                }
            }

            if (StringUtils.isNotBlank(randomKeyOnLmk)) {
                context.put(TxnIC.RESULT_TYPE, RespCmdType.A1);
                context.put(TxnIC.RESULT_CODE, RespCodeIC.SUCCESS);
                context.put(TxnIC.RESULT_DATA, randomKeyOnLmk.toUpperCase() + randomKeyOnZmk.toUpperCase() + checkValue.toUpperCase());
            } else {
                context.put(TxnIC.RESULT_TYPE, RespCmdType.A1);
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
