package com.weelgo.core.exceptions;

import java.io.Serializable;

public class WeelgoFatalException extends WeelgoException implements Serializable {

	public WeelgoFatalException() {
	}

//	public WeelgoFatalException(String strText,int type) {
//		super(strText,type);
//	}
	public WeelgoFatalException(Throwable e, String type) {
		super(e.getMessage(), type);
		// Ici on ne place pas l'exception pour la cause car il faut garder une
		// séparation avec les exceptions extérieures qui ne sont pas forcément
		// sérialisables.

	}

	public WeelgoFatalException(String strText, String type) {
		super(strText, type);

	}

	public WeelgoFatalException(String strText, String type, String[] messageParameters) {
		super(strText, type, messageParameters);
	}

}
