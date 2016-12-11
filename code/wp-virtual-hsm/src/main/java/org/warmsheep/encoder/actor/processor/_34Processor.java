package org.warmsheep.encoder.actor.processor;

import org.jpos.iso.ISOMsg;
import org.jpos.transaction.Context;
import org.warmsheep.encoder.actor.AbsActor;
import org.warmsheep.encoder.bean._34CommandBean;
import org.warmsheep.encoder.constants.KeyConstants;
import org.warmsheep.encoder.constants.RespCmdType;
import org.warmsheep.encoder.ic.RespCodeIC;
import org.warmsheep.encoder.ic.TxnIC;
import org.warmsheep.encoder.security.rsa.RSA;
import org.warmsheep.encoder.security.util.ByteUtil;
import org.warmsheep.encoder.security.util.EncryptUtil;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPublicKey;

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
                KeyPair keyPair = RSA.generateKeyPair(keyLength, commandBean.getKeyIndex());
                RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
                RSAPrivateCrtKey privateKey = (RSAPrivateCrtKey) keyPair.getPrivate();
                int moduleLength = keyLength / 8;
                byte[] pubkey = new byte[moduleLength];
                byte[] prikey = new byte[moduleLength];
                //
                BigInteger n = publicKey.getModulus();
                BigInteger p = privateKey.getPrimeP();
                BigInteger q = privateKey.getPrimeQ();
                /**
                 *  BigInteger 里有一个bit的符号位,所以直接用toByteArray会包含符号位,
                 *  在c的代码里没符号位,所以1024bit的n,java里BigInteger是1025bit长
                 *  直接拷贝128byte出来,正数第一个字节是是0,后面会丢掉最后一字节
                 * */
                System.arraycopy(n.toByteArray(), 1, pubkey, 0, moduleLength);
                int tmp = moduleLength / 2;
                System.arraycopy(p.toByteArray(), 1, prikey, 0, tmp);
                System.arraycopy(q.toByteArray(), 1, prikey, tmp, tmp);

                String privateKeyText = EncryptUtil.desEncryptHexString(ByteUtil.toHexString(prikey), KeyConstants.RSA_034);
                String privateKeyLength = String.format("%04d", privateKeyText.length());
                String publicKeyClearText = ByteUtil.toHexString(pubkey);

                context.put(TxnIC.RESULT_TYPE, RespCmdType._34);
                context.put(TxnIC.RESULT_CODE, RespCodeIC.SUCCESS);
                context.put(TxnIC.RESULT_DATA, privateKeyLength + privateKeyText + publicKeyClearText);
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
