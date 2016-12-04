package org.warmsheep.encoder.bean;

/**
 * Created by qingyuan on 16/12/3.
 */
public class _34CommandBean extends CommandBean {

    private final static int KEY_INDEX_LENGTH = 2;
    private final static int KEY_LENGTH_LENGTH = 4;

    /**
     * 产生RSA密钥对
     */
    private _34CommandBean(){}


    public static  _34CommandBean build(String header,String commandType,String commandContent){
        _34CommandBean _34commandBean = new _34CommandBean();

        _34commandBean.setCommandHeader(header);
        _34commandBean.setCommandType(commandType);

        int index = 0;

        _34commandBean.setKeyLength(commandContent.substring(index, index+=KEY_LENGTH_LENGTH));
        _34commandBean.setKeyIndex(commandContent.substring(index,index+=KEY_INDEX_LENGTH));

        return _34commandBean;
    }

    private String keyLength;
    private String keyIndex;

    /**
     * 比特长度：“0320”－“4096”，应为8的整倍数。
     * @return
     */
    public String getKeyLength() {
        return keyLength;
    }

    /**
     * 比特长度：“0320”－“4096”，应为8的整倍数
     * @param keyLength
     */
    public void setKeyLength(String keyLength) {
        this.keyLength = keyLength;
    }

    public String getKeyIndex() {
        return keyIndex;
    }

    /**
     * 比特长度：“0320”－“4096”，应为8的整倍数
     * @param keyIndex
     */
    public void setKeyIndex(String keyIndex) {
        this.keyIndex = keyIndex;
    }
}
