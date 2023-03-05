package com.weelgo.eclipse.plugin.job;

public interface CMJobHandler {

	public static final String STATUS_CANT_EXECUTE = "cant_execute";
	public static final String STATUS_OK = "ok";
	public static final String STATUS_ERROR = "ERROR";

	public abstract void jobEnded(String status);

}
