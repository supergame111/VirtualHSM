package org.warmsheep.encoder.bean;

import org.warmsheep.encoder.exception.FormatException;

/**
 * Created by qingyuan on 16/12/3.
 */
public class MCCommandBean extends CommandBean{
    private final static int TAK_LENGTH = 16;
    private final static int MAC_LENGTH = 8;
    private final static int MAC_DATA_MAX_LENGTH = 1024;

    private MCCommandBean(){}
    public static MCCommandBean build(String header,String commandType,String commandContent) {
        MCCommandBean mcCommandBean = new MCCommandBean();
        mcCommandBean.setCommandHeader(header);
        mcCommandBean.setCommandType(commandType);

        int index = 0;
        mcCommandBean.setTak(commandContent.substring(index, index+=TAK_LENGTH));
        mcCommandBean.setMac(commandContent.substring(index, index+=MAC_LENGTH));
        if (commandContent.length()-index<MAC_DATA_MAX_LENGTH)
            mcCommandBean.setMacData(commandContent.substring(index));
        else
            throw new FormatException("MC命令MAC数据超过了1024");

        return mcCommandBean;
    }
    private String tak;
    private String mac;
    private String macData;

    public String getTak() {
        return tak;
    }

    public void setTak(String tak) {
        this.tak = tak;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getMacData() {
        return macData;
    }

    public void setMacData(String macData) {
        this.macData = macData;
    }
}
