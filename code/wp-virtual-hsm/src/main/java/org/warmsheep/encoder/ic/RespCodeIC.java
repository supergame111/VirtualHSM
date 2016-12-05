package org.warmsheep.encoder.ic;

public interface RespCodeIC {

	public static final String SUCCESS = "00";//正常响应
	public static final String INVALID_FACTOR_NUM = "03";//无效成分号
	public static final String FORMAT_ERROR = "30";//格式有误
	public static final String VALIDATE_ERROR = "27";//校验错
	public static final String OTHER_ERROR = "28";//未知错误
	public final static String MAC_VALID_FAIL = "01"; //MAC校验失败
	public final static String SIGN_FAIL = "35"; // 签名失败
	public final static String SIGNATURE_VERIFY_FAIL = "39";
}
