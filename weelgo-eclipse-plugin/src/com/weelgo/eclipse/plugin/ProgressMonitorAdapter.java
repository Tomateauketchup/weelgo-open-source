package com.weelgo.eclipse.plugin;

import org.eclipse.core.runtime.SubMonitor;

import com.weelgo.core.IProgressMonitor;

public class ProgressMonitorAdapter {

	public static IProgressMonitor beginTask(String name,int totalWork,org.eclipse.core.runtime.IProgressMonitor monitor) {
		if (monitor != null) {
			monitor.beginTask(name, 100);
			SubMonitor sub = SubMonitor.convert(monitor,totalWork);
			return ProgressMonitorAdapter.adapt(sub);
		}
		return null;
	}

	public static IProgressMonitor adapt(SubMonitor eProgressMonitor) {
		if (eProgressMonitor != null) {
			IProgressMonitor m = new IProgressMonitor() {

				@Override
				public boolean isCanceled() {
					return eProgressMonitor.isCanceled();
				}

				@Override
				public void setWorkRemaining(int workRemaining) {
					eProgressMonitor.setWorkRemaining(workRemaining);

				}

				@Override
				public IProgressMonitor split(int totalWork) {
					SubMonitor newPm = eProgressMonitor.split(totalWork);
					return ProgressMonitorAdapter.adapt(newPm);
				}

				@Override
				public void setTaskName(String name) {
					eProgressMonitor.setTaskName(name);

				}

			};

		}
		return null;
	}

}
