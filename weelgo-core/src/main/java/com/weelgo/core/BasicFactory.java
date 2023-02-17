package com.weelgo.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weelgo.core.exceptions.ExceptionsUtils;

public class BasicFactory implements ICoreFactory {
	private static Logger logger = LoggerFactory.getLogger(BasicFactory.class);

	@Override
	public <T> T instanciate(Class<T> c) {
		try {
			return (T) c.getDeclaredConstructors()[0].newInstance(null);
		} catch (Exception e) {
			ExceptionsUtils.ManageException(e, logger);
		}
		return null;

	}

	@Override
	public ICoreFactory createFactory() {
		return new BasicFactory();
	}

}
