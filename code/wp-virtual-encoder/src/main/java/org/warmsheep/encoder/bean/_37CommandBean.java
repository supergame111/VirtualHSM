package org.warmsheep.encoder.bean;

/**
 * Created by qingyuan on 16/12/3.
 */
public class _37CommandBean extends CommandBean {
    private final static int PADDING_METHOD_LENGTH = 1;
    private final static int PRIVATE_KEY_INDEX_LENGTH = 2;
    private final static int SIGNATURE_DATA_LENGTH = 4;

    /**
     * 用私钥签名（37）
     */
    private _37CommandBean(){}
    public static _37CommandBean build(String header,String commandType,String commandContent){
        _37CommandBean commandBean =new _37CommandBean();

        commandBean.setCommandHeader(header);
        commandBean.setCommandType(commandType);

        int index = 0;

        commandBean.setPaddingMethod(commandContent.substring(index,index+=PADDING_METHOD_LENGTH));
        commandBean.setPrivateKeyIndex(commandContent.substring(index,index+=PRIVATE_KEY_INDEX_LENGTH));
        int signatureDataLength = Integer.parseInt(commandContent.substring(index,index+=SIGNATURE_DATA_LENGTH));
        commandBean.setSignatureData(commandContent.substring(index,index+=signatureDataLength));

        return commandBean;
    }
    private String paddingMethod;
    private String privateKeyIndex;
    private String signatureData;

    public String getPaddingMethod() {
        return paddingMethod;
    }

    /**
     * “0”：如果数据不是密钥长度的整倍数，后面补0x00
     “1”：PKCS填充方式（一般情况下使用此方式）
     * @param paddingMethod
     */
    public void setPaddingMethod(String paddingMethod) {
        this.paddingMethod = paddingMethod;
    }

    public String getPrivateKeyIndex() {
        return privateKeyIndex;
    }

    public void setPrivateKeyIndex(String privateKeyIndex) {
        this.privateKeyIndex = privateKeyIndex;
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
}
