package org.warmsheep.encoder.bean;

public class W2CommandBean extends CommandBean {
	
	private static final long serialVersionUID = 657234656124409629L;

	private W2CommandBean(){}
	
	public static W2CommandBean build(String header,String commandType,String commandContent) {
		W2CommandBean commandBean = new W2CommandBean();
		
		commandBean.setCommandHeader(header);
        commandBean.setCommandType(commandType);
        
        int index=0;
        
        commandBean.setEncModeFlag(commandContent.substring(index, index+=1));
        commandBean.setSolutionId(commandContent.substring(index, index+=1));
        commandBean.setRootKeyType(commandContent.substring(index, index+=3));
        commandBean.setRootKey(commandContent.substring(index, index+=33));
		int tmp = Integer.valueOf(commandContent.substring(index, index+=1));
		commandBean.setDiversifyNum(tmp);
        commandBean.setDiversifyData(commandContent.substring(index, index+=tmp*32));
        commandBean.setSessionKeyFlag(commandContent.substring(index, index+=1));
        if (!commandBean.getSessionKeyFlag().equals("0"))
        	commandBean.setSessionKey(commandContent.substring(index, index+=32));
        commandBean.setDataPaddingFlag(commandContent.substring(index, index+=1));
        tmp = Integer.valueOf(commandContent.substring(index, index+=3));
        commandBean.setDataLen(tmp);
        commandBean.setData(commandContent.substring(index, index+=tmp*2));
        return commandBean;
	}
	/**
	 * 加密模式标识
	 */
	private String encModeFlag;
	/**
	 * 方案ID
	 */
	private String solutionId;
	/**
	 * 根秘钥类型
	 */
	private String rootKeyType;
	/**
	 * 根秘钥
	 */
	private String rootKey;
	/**
	 * 离散次数
	 */
	private int diversifyNum;
	/**
	 * 离散数据
	 */
	private String diversifyData;
	/**
	 * 过程密钥标识
	 */
	private String sessionKeyFlag;
	/**
	 * 过程密钥
	 */
	private String sessionKey;
	/**
	 * 数据填充标识
	 */
	private String dataPaddingFlag;
	/**
	 * 数据长度
	 */
	private int dataLen;
	/**
	 * 数据
	 */
	private String data;
	
	public String getEncModeFlag() {
		return encModeFlag;
	}

	public void setEncModeFlag(String encModeFlag) {
		this.encModeFlag = encModeFlag;
	}

	public String getSolutionId() {
		return solutionId;
	}

	public void setSolutionId(String solutionId) {
		this.solutionId = solutionId;
	}

	public String getRootKeyType() {
		return rootKeyType;
	}

	public void setRootKeyType(String rootKeyType) {
		this.rootKeyType = rootKeyType;
	}

	public String getRootKey() {
		return rootKey;
	}

	public void setRootKey(String rootKey) {
		this.rootKey = rootKey;
	}

	public int getDiversifyNum() {
		return diversifyNum;
	}

	public void setDiversifyNum(int diversifyNum) {
		this.diversifyNum = diversifyNum;
	}

	public String getDiversifyData() {
		return diversifyData;
	}

	public void setDiversifyData(String diversifyData) {
		this.diversifyData = diversifyData;
	}

	public String getSessionKeyFlag() {
		return sessionKeyFlag;
	}

	public void setSessionKeyFlag(String sessionKeyFlag) {
		this.sessionKeyFlag = sessionKeyFlag;
	}

	public String getSessionKey() {
		return sessionKey;
	}

	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
	}

	public String getDataPaddingFlag() {
		return dataPaddingFlag;
	}

	public void setDataPaddingFlag(String dataPaddingFlag) {
		this.dataPaddingFlag = dataPaddingFlag;
	}

	public int getDataLen() {
		return dataLen;
	}

	public void setDataLen(int dataLen) {
		this.dataLen = dataLen;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
