package org.warmsheep.encoder.actor.processor;

import org.jpos.iso.ISOMsg;
import org.jpos.transaction.Context;
import org.warmsheep.encoder.actor.AbsActor;
import org.warmsheep.encoder.bean.MCCommandBean;
import org.warmsheep.encoder.constants.KeyConstants;
import org.warmsheep.encoder.constants.RespCmdType;
import org.warmsheep.encoder.ic.RespCodeIC;
import org.warmsheep.encoder.ic.TxnIC;
import org.warmsheep.encoder.security.mac.impl.ANSIX919;
import org.warmsheep.encoder.security.util.ByteUtil;
import org.warmsheep.encoder.security.util.EncryptUtil;

import java.io.Serializable;

/**
 * Created by qingyuan on 16/12/3.
 */
public class MCProcessor extends AbsActor {
    @Override
    public int prepare(long id, Serializable serializable) {
        Context context = (Context) serializable;
        try {
            ISOMsg reqMsg = (ISOMsg) context.get(TxnIC.MSG_HSM);
            String header = reqMsg.getString(0);
            String commandType = reqMsg.getString(1);
            String requestData = reqMsg.getString(2);

            MCCommandBean mcCommandBean = MCCommandBean.build(header,commandType,requestData);
            String takClearText = EncryptUtil.desDecryptToHex(mcCommandBean.getTak(), KeyConstants.TAK_003);

            ANSIX919 ansix919 = new ANSIX919();
            byte[] tak = ByteUtil.hexStringToByte(takClearText);
            byte[] src = ByteUtil.hexStringToByte(mcCommandBean.getMacData());
            byte[] mac = ansix919.getMac(src,tak);
            String macstr = ByteUtil.bytesToHexString(mac);

            if (mcCommandBean.getMac().equals(macstr)){
                context.put(TxnIC.RESULT_TYPE, RespCmdType.MD);
                context.put(TxnIC.RESULT_CODE, RespCodeIC.SUCCESS);
            } else {
                context.put(TxnIC.RESULT_TYPE, RespCmdType.MD);
                context.put(TxnIC.RESULT_CODE, RespCodeIC.MAC_VALID_FAIL);
            }
            return PREPARED | NO_JOIN;

        } catch (Exception e) {
            logger.error("MC指令处理出现异常", e);
            context.put(TxnIC.RESULT_CODE, RespCodeIC.OTHER_ERROR);
            return ABORTED | NO_JOIN;
        }
    }
}
