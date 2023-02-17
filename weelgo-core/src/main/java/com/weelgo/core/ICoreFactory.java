package com.weelgo.core;

public interface ICoreFactory {
	
	public <T> T instanciate(Class<T> c);
	public ICoreFactory createFactory();
	

}
