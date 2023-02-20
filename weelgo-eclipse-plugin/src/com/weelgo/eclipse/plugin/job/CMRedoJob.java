package com.weelgo.eclipse.plugin.job;

import org.eclipse.e4.core.di.annotations.Creatable;

import com.weelgo.eclipse.plugin.CMEvents;
import com.weelgo.eclipse.plugin.Factory;

@Creatable
public class CMRedoJob extends CMJob {

	public CMRedoJob() {
		super("Redo module", "Redoing module ...");
	}

	public static CMRedoJob CREATE() {
		return Factory.create(CMRedoJob.class);
	}

	@Override
	public void doRun(com.weelgo.core.IProgressMonitor monitor) {

		getModulesManager().redoModelForUndoRedo(getModuleUniqueIdentifier());
		sentEvent(CMEvents.MODULE_UNDO_REDO_OPERATION_DONE, getModuleUniqueIdentifier());

	}
}
