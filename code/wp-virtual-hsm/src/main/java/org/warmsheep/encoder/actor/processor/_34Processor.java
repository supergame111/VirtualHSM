package org.warmsheep.encoder.actor.processor;

import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOUtil;
import org.jpos.transaction.Context;
import org.warmsheep.encoder.actor.AbsActor;
import org.warmsheep.encoder.bean._34CommandBean;
import org.warmsheep.encoder.constants.KeyConstants;
import org.warmsheep.encoder.constants.RespCmdType;
import org.warmsheep.encoder.ic.RespCodeIC;
import org.warmsheep.encoder.ic.TxnIC;
import org.warmsheep.encoder.security.rsa.RSA;
import org.warmsheep.encoder.security.util.EncryptUtil;

import java.io.Serializable;

/**
 * Created by qingyuan on 16/12/3.
 */
public class _34Processor extends AbsActor {
    @Override
    public int prepare(long id, Serializable serializable) {
        Context context = (Context) serializable;
        try {
            ISOMsg reqMsg = (ISOMsg) context.get(TxnIC.MSG_HSM);
            String header = reqMsg.getString(0);
            String commandType = reqMsg.getString(1);
            String requestData = reqMsg.getString(2);

            _34CommandBean commandBean = _34CommandBean.build(header, commandType, requestData);

            int keyLength = Integer.parseInt(commandBean.getKeyLength());
            if (keyLength % 8 == 0) {
                RSA.generateKeyPair(keyLength, commandBean.getKeyIndex());
                String privateKey = EncryptUtil.desEncryptHexString(ISOUtil.hexString(RSA.getPrivateKeyBytes(commandBean.getKeyIndex())), KeyConstants.RSA_034);
                String privateKeyLength = String.format("%04d",privateKey.length());
                String publicKey = RSA.getPublicKey(commandBean.getKeyIndex());
                context.put(TxnIC.RESULT_TYPE, RespCmdType._34);
                context.put(TxnIC.RESULT_CODE, RespCodeIC.SUCCESS);
                context.put(TxnIC.RESULT_DATA, privateKeyLength+ privateKey + publicKey);
            } else {
                context.put(TxnIC.RESULT_TYPE, RespCmdType._34);
                context.put(TxnIC.RESULT_CODE, RespCodeIC.FORMAT_ERROR);
            }

            return PREPARED | NO_JOIN;

        } catch (Exception e) {
            logger.error("34指令处理出现异常", e);
            context.put(TxnIC.RESULT_CODE, RespCodeIC.OTHER_ERROR);
            return ABORTED | NO_JOIN;
        }
    }
}
