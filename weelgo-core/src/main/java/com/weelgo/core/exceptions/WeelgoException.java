package com.weelgo.core.exceptions;

import java.io.Serializable;

public class WeelgoException extends RuntimeException implements Serializable {

	private String[] messagrParameters;

	private String type = NO_DEF;
	public static final String NO_DEF = "not_defined";
	public static final String FATAL_EXTERNAL = "fatal_external";
	public static final String INVALID_INPUT = "invalid_input";
	public static final String INVALID_TASK_NAME = "invalid_task_name";
	public static final String INVALID_NEED_NAME = "invalid_need_name";
	public static final String INVALID_GROUP_NAME = "invalid_group_name";
	public static final String INVALID_PACKAGE_NAME = "invalid_package_name";
	public static final String OBJECT_ALREADY_EXIST = "object_already_exists";
	public static final String MODULE_ALREADY_EXIST = "module_already_exists";
	public static final String OBJECT_NULL_OR_EMPTY = "object_null_or_empty";
	public static final String GROUP_ALREADY_EXIST = "group_already_exists";

	public WeelgoException() {
	}

	public WeelgoException(Throwable e, String type) {
		super(e);
		this.type = type;
	}

	public WeelgoException(String strText, String type) {
		super(strText == null || strText.isEmpty() ? "weelgo_exception." + String.valueOf(type) : strText);
		this.type = type;
	}

	public WeelgoException(String strText, String type, String[] messageParameters) {
		super(strText == null || strText.isEmpty() ? "weelgo_exception." + String.valueOf(type) : strText);
		this.type = type;
		this.messagrParameters = messageParameters;
	}

	public String getType() {
		return type;
	}

	public String[] getMessagrParameters() {
		return messagrParameters;
	}

	public void setMessagrParameters(String[] messagrParameters) {
		this.messagrParameters = messagrParameters;
	}

}
