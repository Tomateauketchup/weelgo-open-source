package com.weelgo.core.exceptions;

public class AssertNotNullOrEmpty {

	public AssertNotNullOrEmpty(Object o) {
		init(o, null);
	}

	public AssertNotNullOrEmpty(Object o, String message) {
		init(o, message);
	}

	public void init(Object o, String message) {
		if (o == null)
			ExceptionsUtils.throwDynamicException(WeelgoException.OBJECT_NULL_OR_EMPTY, message);
		if (o != null && o instanceof String && o.equals(""))
			ExceptionsUtils.throwDynamicException(WeelgoException.OBJECT_NULL_OR_EMPTY, message);
	}
}