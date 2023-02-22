package com.weelgo.eclipse.plugin.job;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.di.annotations.Execute;

import com.weelgo.eclipse.plugin.CMEvents;
import com.weelgo.eclipse.plugin.Factory;
import com.weelgo.eclipse.plugin.ImagesFactory;

@Creatable
public class CMLoadModuleJob extends CMJob {

	public CMLoadModuleJob() {
		super("Load module", "Loading module ...");
	}

	public static CMLoadModuleJob CREATE() {
		return Factory.create(CMLoadModuleJob.class);
	}

	@Override
	public String getUndoRedoLabel() {
		return "Load module";
	}

	@Override
	public String getUndoRedoIcon() {
		return ImagesFactory.LOAD_ICON;
	}
	
	@Override
	public boolean isUndoRedoJob() {
		return true;
	}	

	@Override
	public void doRun(com.weelgo.core.IProgressMonitor monitor) {

		String id = getModuleUniqueIdentifier();
		getServices().getModulesManager().loadModule(id, monitor);

		sentEvent(CMEvents.MODULE_LOADED, id);

	}

	@Execute
	public void execute() {
		doSchedule();
	}

}
