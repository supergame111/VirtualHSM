package org.warmsheep.encoder.actor.processor;

import org.jpos.transaction.Context;
import org.warmsheep.encoder.actor.AbsActor;
import org.warmsheep.encoder.constants.RespCmdType;
import org.warmsheep.encoder.ic.RespCodeIC;
import org.warmsheep.encoder.ic.TxnIC;

import java.io.Serializable;

/**
 * Created by ft on 2017/5/5.
 */
public class NCProcessor extends AbsActor {
    @Override
    public int prepare(long id, Serializable serializable) {
        Context context = (Context) serializable;
        context.put(TxnIC.RESULT_TYPE, RespCmdType.NC);
        context.put(TxnIC.RESULT_CODE, RespCodeIC.SUCCESS);
        return PREPARED | NO_JOIN;
    }
}
