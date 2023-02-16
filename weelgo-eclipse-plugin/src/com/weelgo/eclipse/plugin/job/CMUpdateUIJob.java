package com.weelgo.eclipse.plugin.job;

import java.util.function.Consumer;

import org.eclipse.e4.core.di.annotations.Creatable;

import com.weelgo.core.IProgressMonitor;

@Creatable
public abstract class CMUpdateUIJob extends CMJob {

	public CMUpdateUIJob(String jobName) {
		super(jobName);
	}

	public CMUpdateUIJob(String jobName, String beginTaskMessage) {
		super(jobName, beginTaskMessage);
	}

	public CMUpdateUIJob() {
		super("Update Weelgo UI", "Updating Weelgo UI ...");
	}

	public static CMUpdateUIJob CREATE(String jobName, String jobMessageName, Consumer<IProgressMonitor> c) {
		CMUpdateUIJob j = new CMUpdateUIJob(jobName, jobMessageName) {

			@Override
			public void doRun(IProgressMonitor monitor) {
				c.accept(monitor);
			}
		};
		j.createServices();
		return j;
	}

}
