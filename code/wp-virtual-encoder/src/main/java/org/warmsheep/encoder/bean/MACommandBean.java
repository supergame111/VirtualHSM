package org.warmsheep.encoder.bean;

import org.warmsheep.encoder.exception.FormatException;

/**
 * Created by qingyuan on 16/12/3.
 */
public class MACommandBean extends CommandBean {

    private final static int TAK_LENGTH = 16;
    private final static int MAC_DATA_MAX_LENGTH = 1024;

    private MACommandBean(){}

    public static MACommandBean build(String header,String commandType,String commandContent) {
        MACommandBean maCommandBean = new MACommandBean();
        maCommandBean.setCommandHeader(header);
        maCommandBean.setCommandType(commandType);

        int index = 0;
        maCommandBean.setTak(commandContent.substring( index, index += TAK_LENGTH));
        if (commandContent.length()-index <MAC_DATA_MAX_LENGTH)
            maCommandBean.setMacData(commandContent.substring(index));
        else
            throw new FormatException("MA命令MAC数据超过了1024");

        return maCommandBean;
    }

    private String tak;
    private String macData;

    public String getTak() {
        return tak;
    }

    /**
     * LMK对（16-17）下加密的TAK
     * @param tak
     */
    public void setTak(String tak) {
        this.tak = tak;
    }

    public String getMacData() {
        return macData;
    }

    /**
     * 生成MAC的数据，n=1024（在SNA-SDLC系统中为512）
     * @param macData
     */
    public void setMacData(String macData) {
        this.macData = macData;
    }
}
