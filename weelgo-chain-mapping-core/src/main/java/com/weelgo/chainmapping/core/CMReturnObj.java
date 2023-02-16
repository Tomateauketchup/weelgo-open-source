package com.weelgo.chainmapping.core;

public class CMReturnObj {

	public CMModuleService service;
	public Object object;

	public static CMModuleService getServices(CMReturnObj ret) {
		if (ret != null) {
			return ret.service;
		}
		return null;
	}

	public static Object getObject(CMReturnObj ret) {
		if (ret != null) {
			return ret.object;
		}
		return null;
	}
}
