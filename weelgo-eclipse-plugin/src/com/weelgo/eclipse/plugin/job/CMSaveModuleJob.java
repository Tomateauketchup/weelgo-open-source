package com.weelgo.eclipse.plugin.job;

import com.weelgo.core.IProgressMonitor;
import com.weelgo.eclipse.plugin.CMEvents;
import com.weelgo.eclipse.plugin.Factory;

public class CMSaveModuleJob extends CMDatabaseModifierJob {

	public CMSaveModuleJob() {
		super("Save module", "Saving module ...");
	}

	public static CMSaveModuleJob CREATE() {
		return Factory.create(CMSaveModuleJob.class);
	}

	@Override
	public void doRun(IProgressMonitor monitor) {

		getServices().getModulesManager().saveModule(monitor, getModuleUniqueIdentifier());

		sentEvent(CMEvents.MODULE_SAVED, getModuleUniqueIdentifier());
	}

}
