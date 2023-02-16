package com.weelgo.core;

public interface IProgressMonitor {
	public boolean isCanceled();

	public IProgressMonitor split(int totalWork);

	public void setWorkRemaining(int totalWork);

	public void setTaskName(String name);

}
