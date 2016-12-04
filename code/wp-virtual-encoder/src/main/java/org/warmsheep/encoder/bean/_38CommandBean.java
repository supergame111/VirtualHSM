package org.warmsheep.encoder.bean;

import org.warmsheep.encoder.exception.FormatException;

/**
 * Created by qingyuan on 16/12/3.
 */
public class _38CommandBean extends CommandBean {
    private final static int PADDING_METHOD_LENGTH = 1;
    private final static int SIGNATURE_LENGTH = 4;
    private final static int SPLIT_SIGN_LENGTH = 1;
    private final static int SIGNATURE_DATA_LENGTH = 4;

    /**
     * 公钥验证签名
     */
    private _38CommandBean() {}

    public static _38CommandBean build(String header,String commandType,String commandContent) {
        _38CommandBean commandBean = new _38CommandBean();

        commandBean.setCommandHeader(header);
        commandBean.setCommandType(commandType);

        int index=0;
        commandBean.setPaddingMethod(commandContent.substring(index,index+=PADDING_METHOD_LENGTH));
        int signatureLength = Integer.parseInt(commandContent.substring(index,index+=SIGNATURE_LENGTH));
        commandBean.setSignature(commandContent.substring(index,index+=signatureLength));
        if (commandContent.substring(index,index+=SPLIT_SIGN_LENGTH).equals(";")){
            int signatureDataLength = Integer.parseInt(commandContent.substring(index,index+=SIGNATURE_DATA_LENGTH));
            commandBean.setSignatureData(commandContent.substring(index,index+=signatureDataLength));
            if (commandContent.substring(index,index+=SPLIT_SIGN_LENGTH).equals(";"))
                commandBean.setPublicKey(commandContent.substring(index));
            else
                throw new FormatException("38命令数据格式解析失败，第二个分隔符没有找到");
        } else
            throw new FormatException("38命令数据格式解析失败，第一个分隔符没有找到");

        return commandBean;
    }
    private String paddingMethod;
    private String signature;
    private String signatureData;
    private String publicKey;

    public String getPaddingMethod() {
        return paddingMethod;
    }

    /**
     * “0”：如果数据不是密钥长度的整倍数，后面补0x00
     “1”：PKCS填充方式
     * @param paddingMethod
     */
    public void setPaddingMethod(String paddingMethod) {
        this.paddingMethod = paddingMethod;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getSignatureData() {
        return signatureData;
    }

    /**
     * 用于签名的数据
     * @param signatureData
     */
    public void setSignatureData(String signatureData) {
        this.signatureData = signatureData;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
}
