package com.weelgo.core;

public class ProgressMonitorUtils {

	public static boolean isCanceled(IProgressMonitor monitor) {
		if (monitor != null) {
			return monitor.isCanceled();
		}
		return false;
	}

	public static IProgressMonitor split(IProgressMonitor monitor, int totalWork) {
		if (monitor != null) {
			return monitor.split(totalWork);
		}
		return null;
	}

	public static void setWorkRemaining(IProgressMonitor monitor, int totalWork) {
		if (monitor != null) {
			monitor.setWorkRemaining(totalWork);
		}
	}

	public static void setTaskName(IProgressMonitor monitor, String name) {
		if (monitor != null) {
			monitor.setTaskName(name);
		}
	}

}
