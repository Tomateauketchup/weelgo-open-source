package com.weelgo.eclipse.plugin.job;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.di.annotations.Execute;

import com.weelgo.eclipse.plugin.CMEvents;
import com.weelgo.eclipse.plugin.Factory;
import com.weelgo.eclipse.plugin.ImagesFactory;

@Creatable
public class CMLoadAllModulesJob extends CMJob {

	public CMLoadAllModulesJob() {
		super("Load all modules", "Loading all modules ...");
	}

	public static CMLoadAllModulesJob CREATE() {
		return Factory.create(CMLoadAllModulesJob.class);
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
	public boolean isUndoRedoAllModulesJob() {
		return true;
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
	
	//TODO tester la création d'un module alors qu'il y a déjà un module qui existe dans l'IHM. Ca ne met pas de message d'erreur.
	//TODO quand on supprime le dossier d'un module et qu'on fais un load all, ça supprime pas le module des services

}
