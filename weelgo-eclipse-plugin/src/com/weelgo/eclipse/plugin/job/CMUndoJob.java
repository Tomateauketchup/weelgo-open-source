package com.weelgo.eclipse.plugin.job;

import org.eclipse.e4.core.di.annotations.Creatable;

import com.weelgo.eclipse.plugin.CMEvents;
import com.weelgo.eclipse.plugin.Factory;

@Creatable
public class CMUndoJob extends CMJob {

	public CMUndoJob() {
		super("Undo module", "Undoing module ...");
	}

	public static CMUndoJob CREATE() {
		return Factory.create(CMUndoJob.class);
	}

	@Override
	public void doRun(com.weelgo.core.IProgressMonitor monitor) {

		getModulesManager().undoModelForUndoRedo(getModuleUniqueIdentifier());
		sentEvent(CMEvents.MODULE_UNDO_REDO_OPERATION_DONE, getModuleUniqueIdentifier());

	}
}
