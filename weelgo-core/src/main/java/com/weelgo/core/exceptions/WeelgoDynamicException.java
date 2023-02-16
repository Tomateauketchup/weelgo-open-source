package com.weelgo.core.exceptions;

import java.io.Serializable;

public class WeelgoDynamicException extends WeelgoException implements Serializable {

	public WeelgoDynamicException() {
	}

//	public WeelgoDynamicException(String strText,int type) {
//		super(strText,type);
//	}

	public WeelgoDynamicException(String strText, String type) {
		super(strText, type);

	}

	public WeelgoDynamicException(String strText, String type, String[] messageParameters) {
		super(strText, type, messageParameters);
	}

}
