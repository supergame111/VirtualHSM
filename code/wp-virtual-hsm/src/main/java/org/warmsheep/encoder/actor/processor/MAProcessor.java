package org.warmsheep.encoder.actor.processor;

import org.apache.commons.lang3.StringUtils;
import org.jpos.iso.ISOMsg;
import org.jpos.transaction.Context;
import org.warmsheep.encoder.actor.AbsActor;
import org.warmsheep.encoder.bean.MACommandBean;
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
public class MAProcessor extends AbsActor {
    @Override
    public int prepare(long id, Serializable serializable) {
        Context context = (Context) serializable;
        try {
            ISOMsg reqMsg = (ISOMsg) context.get(TxnIC.MSG_HSM);
            String header = reqMsg.getString(0);
            String commandType = reqMsg.getString(1);
            String requestData = reqMsg.getString(2);

            MACommandBean maCommandBean = MACommandBean.build(header,commandType,requestData);

            String takClearText = EncryptUtil.desDecryptToHex(maCommandBean.getTak(), KeyConstants.TAK_003);

            ANSIX919 ansix919 = new ANSIX919();
            byte[] tak = ByteUtil.hexStringToByte(takClearText);
            byte[] src = ByteUtil.hexStringToByte(maCommandBean.getMacData());
            byte[] mac = ansix919.getMac(src,tak);

            String macstr = ByteUtil.bytesToHexString(mac);

            if (StringUtils.isNotBlank(macstr)) {
                context.put(TxnIC.RESULT_TYPE, RespCmdType.MB);
                context.put(TxnIC.RESULT_CODE, RespCodeIC.SUCCESS);
                context.put(TxnIC.RESULT_DATA, maCommandBean.getTak().toUpperCase() + macstr.toUpperCase());
            } else {
                context.put(TxnIC.RESULT_TYPE, RespCmdType.MB);
                //TODO待实现
                context.put(TxnIC.RESULT_CODE, RespCodeIC.FORMAT_ERROR);
            }
            return PREPARED | NO_JOIN;

        } catch (Exception e) {
            logger.error("MA指令处理出现异常", e);
            context.put(TxnIC.RESULT_CODE, RespCodeIC.OTHER_ERROR);
            return ABORTED | NO_JOIN;
        }
    }
}
