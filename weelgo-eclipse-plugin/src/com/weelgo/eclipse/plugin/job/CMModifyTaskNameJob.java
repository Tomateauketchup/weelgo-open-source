package com.weelgo.eclipse.plugin.job;

import com.weelgo.chainmapping.core.CMModuleService;
import com.weelgo.chainmapping.core.CMNode;
import com.weelgo.chainmapping.core.CMTask;
import com.weelgo.core.IProgressMonitor;
import com.weelgo.eclipse.plugin.CMEvents;
import com.weelgo.eclipse.plugin.Factory;
import com.weelgo.eclipse.plugin.ImagesFactory;

public class CMModifyTaskNameJob extends CMJob {

	private String newName;

	public static CMModifyTaskNameJob CREATE() {
		return Factory.create(CMModifyTaskNameJob.class);
	}

	public CMModifyTaskNameJob() {
		super("Modify task name", "Modifying task name ...");
	}

	@Override
	public String getUndoRedoLabel() {
		return "Modify task name";
	}

	@Override
	public String getUndoRedoIcon() {
		return ImagesFactory.MODIFY_ICON;
	}

	@Override
	public boolean isUndoRedoJob() {
		return true;
	}

	@Override
	public void doRun(IProgressMonitor monitor) {

		CMNode nd = getSelectedObject(CMNode.class);
		CMModuleService ser = getModuleService(nd);
		if (ser != null) {
			setModuleUniqueIdentifier(ser.getModuleUniqueIdentifier());
			CMTask tsk = ser.modifyTaskName(newName, nd.getUuid());
			sentEvent(CMEvents.TASK_NAME_MODIFIED, tsk);
		}

	}

	public String getNewName() {
		return newName;
	}

	public void setNewName(String newName) {
		this.newName = newName;
	}

}
