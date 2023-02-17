package com.weelgo.chainmapping.core;

import com.weelgo.core.BasicFactory;

public class CMFactory extends BasicFactory {

	private static CMFactory factory = new CMFactory();

	public static <T> T create(Class<T> c) {
		return factory.instanciate(c);
	}

}
