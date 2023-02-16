package com.weelgo.eclipse.plugin.job;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.di.annotations.Execute;

import com.weelgo.eclipse.plugin.CMEvents;
import com.weelgo.eclipse.plugin.Factory;

@Creatable
public class CMLoadAllModulesJob extends CMJob {

	public CMLoadAllModulesJob() {
		super("Load all modules", "Loading all modules ...");
	}

	public static CMLoadAllModulesJob CREATE()
	{
		return Factory.create(CMLoadAllModulesJob.class);
	}
	
	@Override
	public void doRun(com.weelgo.core.IProgressMonitor monitor) {

		getServices().loadModules(monitor);

		sentEvent(CMEvents.ALL_MODULE_LOADED);

	}

	@Execute
	public void execute() {		
		doSchedule();
	}
	
}
