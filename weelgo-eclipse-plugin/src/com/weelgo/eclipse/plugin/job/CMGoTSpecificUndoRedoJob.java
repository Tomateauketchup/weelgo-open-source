package com.weelgo.eclipse.plugin.job;

import org.eclipse.e4.core.di.annotations.Creatable;

import com.weelgo.core.undoredo.UndoRedoNode;
import com.weelgo.eclipse.plugin.CMEvents;
import com.weelgo.eclipse.plugin.Factory;

@Creatable
public class CMGoTSpecificUndoRedoJob extends CMJob {

	private UndoRedoNode node;

	public CMGoTSpecificUndoRedoJob() {
		super("Go to specific undo/redo", "Going to specific undo/redo ...");
	}

	public static CMGoTSpecificUndoRedoJob CREATE() {
		return Factory.create(CMGoTSpecificUndoRedoJob.class);
	}

	@Override
	public void doRun(com.weelgo.core.IProgressMonitor monitor) {

		getModulesManager().goToSpecificUndoRedoSave(getModuleUniqueIdentifier(), getNode());
		sentEvent(CMEvents.MODULE_UNDO_REDO_OPERATION_DONE, getModuleUniqueIdentifier());

	}

	public UndoRedoNode getNode() {
		return node;
	}

	public void setNode(UndoRedoNode node) {
		this.node = node;
	}

}
