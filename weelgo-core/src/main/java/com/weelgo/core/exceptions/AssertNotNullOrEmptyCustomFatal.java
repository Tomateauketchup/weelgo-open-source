package com.weelgo.core.exceptions;
public class AssertNotNullOrEmptyCustomFatal {

	public AssertNotNullOrEmptyCustomFatal(Object o,String message){
		if(o==null)ExceptionsUtils.throwException(message);
		if(o!=null && o instanceof String && o.equals(""))ExceptionsUtils.throwException(message);
	}

}