package com.weelgo.core;

public class CoreFactory extends BasicFactory {

	private static CoreFactory factory = new CoreFactory();

	public static <T> T create(Class<T> c) {
		return factory.instanciate(c);
	}

}
