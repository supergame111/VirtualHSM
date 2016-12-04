package org.warmsheep.encoder.actor.processor;

import org.jpos.iso.ISOMsg;
import org.jpos.transaction.Context;
import org.warmsheep.encoder.actor.AbsActor;
import org.warmsheep.encoder.bean._37CommandBean;
import org.warmsheep.encoder.constants.RespCmdType;
import org.warmsheep.encoder.ic.RespCodeIC;
import org.warmsheep.encoder.ic.TxnIC;
import org.warmsheep.encoder.security.rsa.RSA;
import org.warmsheep.encoder.security.rsa.RSAHelper;
import org.warmsheep.encoder.util.StringUtil;

import java.io.Serializable;

/**
 * Created by qingyuan on 16/12/3.
 */
public class _37Processor extends AbsActor {
    @Override
    public int prepare(long id, Serializable serializable) {
        Context context = (Context) serializable;
        try {
            ISOMsg reqMsg = (ISOMsg) context.get(TxnIC.MSG_HSM);
            String header = reqMsg.getString(0);
            String commandType = reqMsg.getString(1);
            String requestData = reqMsg.getString(2);

            _37CommandBean commandBean = _37CommandBean.build(header, commandType, requestData);

            String privateKeyIndex = commandBean.getPrivateKeyIndex();
            String signatureData = commandBean.getSignatureData();

            String signature = RSAHelper.sign(signatureData.getBytes(), RSA.getPrivateKey(privateKeyIndex));

            if (!StringUtil.isEmpty(signature)) {
                String signatureLength = String.format("%04d", signature.length());
                context.put(TxnIC.RESULT_TYPE, RespCmdType._37);
                context.put(TxnIC.RESULT_CODE, RespCodeIC.SUCCESS);
                context.put(TxnIC.RESULT_DATA, signatureLength + signature);
            } else {
                context.put(TxnIC.RESULT_TYPE, RespCmdType._37);
                context.put(TxnIC.RESULT_CODE, RespCodeIC.SIGN_FAIL);
            }

            return PREPARED | NO_JOIN;
        } catch (Exception e) {
            logger.error("37指令处理出现异常", e);
            context.put(TxnIC.RESULT_CODE, RespCodeIC.OTHER_ERROR);
            return ABORTED | NO_JOIN;
        }

    }
}
