package org.warmsheep.encoder.actor.processor;

import org.jpos.iso.ISOMsg;
import org.jpos.transaction.Context;
import org.warmsheep.encoder.actor.AbsActor;
import org.warmsheep.encoder.bean._38CommandBean;
import org.warmsheep.encoder.constants.RespCmdType;
import org.warmsheep.encoder.ic.RespCodeIC;
import org.warmsheep.encoder.ic.TxnIC;
import org.warmsheep.encoder.security.rsa.RSAHelper;

import java.io.Serializable;

/**
 * Created by qingyuan on 16/12/3.
 */
public class _38Processor extends AbsActor {
    @Override
    public int prepare(long id, Serializable serializable) {
        Context context = (Context) serializable;
        try {
            ISOMsg reqMsg = (ISOMsg) context.get(TxnIC.MSG_HSM);
            String header = reqMsg.getString(0);
            String commandType = reqMsg.getString(1);
            String requestData = reqMsg.getString(2);

            _38CommandBean commandBean = _38CommandBean.build(header, commandType, requestData);
            String signature = commandBean.getSignature();
            String signatureData = commandBean.getSignatureData();
            String publicKey = commandBean.getPublicKey();
            if (RSAHelper.verify(signatureData.getBytes(),publicKey,signature)){
                context.put(TxnIC.RESULT_TYPE, RespCmdType._38);
                context.put(TxnIC.RESULT_CODE, RespCodeIC.SUCCESS);
            } else {
                context.put(TxnIC.RESULT_TYPE, RespCmdType._38);
                context.put(TxnIC.RESULT_CODE, RespCodeIC.SIGNATURE_VERIFY_FAIL);
            }
            return PREPARED | NO_JOIN;
        } catch (Exception e){
            logger.error("38指令处理出现异常", e);
            context.put(TxnIC.RESULT_CODE, RespCodeIC.OTHER_ERROR);
            return ABORTED | NO_JOIN;
        }

        }
}
