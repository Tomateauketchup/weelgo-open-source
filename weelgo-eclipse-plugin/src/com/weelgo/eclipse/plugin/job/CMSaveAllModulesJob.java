package com.weelgo.eclipse.plugin.job;

import org.eclipse.e4.core.di.annotations.Execute;

import com.weelgo.core.IProgressMonitor;
import com.weelgo.eclipse.plugin.CMEvents;
import com.weelgo.eclipse.plugin.Factory;

public class CMSaveAllModulesJob extends CMDatabaseModifierJob {

	public static CMSaveAllModulesJob CREATE() {
		return Factory.create(CMSaveAllModulesJob.class);
	}

	public CMSaveAllModulesJob() {
		super("Save all modules", "Saving all modules ...");
	}

	@Override
	public void doRun(IProgressMonitor monitor) {
		getServices().saveAllModules(monitor);

		sentEvent(CMEvents.ALL_MODULE_SAVED);
	}

	@Execute
	public void execute() {
		doSchedule();
	}

}
