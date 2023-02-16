package com.weelgo.chainmapping.core;

import java.util.HashMap;
import java.util.Map;

public enum CMGroupType {
	GROUP("group"), MODULE("module");

	private String code;

	private CMGroupType(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	private static final Map<String, CMGroupType> lookup = new HashMap<String, CMGroupType>();

	static {
		for (CMGroupType d : CMGroupType.values()) {
			lookup.put(d.getCode(), d);
		}
	}

	public static CMGroupType get(String code) {
		return lookup.get(code);
	}

	public static boolean isCodeValid(String code) {
		return get(code) != null;
	}
}