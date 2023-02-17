package com.weelgo.core;

public interface ICloneableObject<T> {

	public default T cloneObject() {
		T o = createThisObject();
		populateObject(o);
		return o;
	}

	public void populateObject(T toPopulate);

	public T createThisObject();

}
