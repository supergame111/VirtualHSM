package org.warmsheep.encoder.bean;

import org.warmsheep.encoder.exception.FormatException;

/**
 * Created by qingyuan on 16-12-5.
 */
public class A4CommandBean extends CommandBean{
    private final static int FACTOR_NUM_LENGTH = 1;
    private final static int KEY_TYPE_LENGTH = 3;
    private static final int LMK_KEY_FLAG_LENGTH = 1;	//lmk标识域长度

    private static final int DOUBLE_KEY_ADD_ONE_LENGTH = 33;		//双倍长密钥域长度33（带X）
    private static final int DOUBLE_KEY_LENGTH = 32;		//双倍长密钥域长度32（不带X）
    private static final int SINGLE_KEY_LENGTH = 16;		//单倍长密钥域长度

    private A4CommandBean(){}
    public static A4CommandBean build(String header,String commandType,String commandContent){
        A4CommandBean a4CommandBean = new A4CommandBean();

        a4CommandBean.setCommandHeader(header);
        a4CommandBean.setCommandType(commandType);

        int index = 0;
        a4CommandBean.setFactorNum(commandContent.substring(index, index+=FACTOR_NUM_LENGTH));
        a4CommandBean.setKeyType(commandContent.substring(index,index+=KEY_TYPE_LENGTH));
        a4CommandBean.setLmkKeyFlag(commandContent.substring(index,index+=LMK_KEY_FLAG_LENGTH));

        int facrotNum = Integer.parseInt(a4CommandBean.getFactorNum());
        try {
            if (commandContent.substring(index, index + 1).equalsIgnoreCase("X")) {
                String[] tmp = new String[facrotNum];
                for (int i = 0; i < tmp.length; i++) {
                    tmp[i] = commandContent.substring(index+1, index += DOUBLE_KEY_ADD_ONE_LENGTH);
                }
                a4CommandBean.setFactors(tmp);
            } else {
                String[] tmp = new String[facrotNum];
                for (int i = 0; i < tmp.length; i++) {
                    tmp[i] = commandContent.substring(index, index += SINGLE_KEY_LENGTH);
                }
                a4CommandBean.setFactors(tmp);
            }
        } catch (Exception e) {
            throw new FormatException("成分的加密方案必须相同"+e.getMessage());
        }

        return a4CommandBean;
    }

    private String factorNum;
    private String keyType;				//密钥类型
    private String lmkKeyFlag;			//LMK密钥标识
    private String[] factors;           //密钥成分

    public String getFactorNum() {
        return factorNum;
    }

    public void setFactorNum(String factorNum) {
        this.factorNum = factorNum;
    }

    public String getKeyType() {
        return keyType;
    }

    public void setKeyType(String keyType) {
        this.keyType = keyType;
    }

    public String getLmkKeyFlag() {
        return lmkKeyFlag;
    }

    public void setLmkKeyFlag(String lmkKeyFlag) {
        this.lmkKeyFlag = lmkKeyFlag;
    }

    public String[] getFactors() {
        return factors;
    }

    public void setFactors(String[] factors) {
        this.factors = factors;
    }
}
