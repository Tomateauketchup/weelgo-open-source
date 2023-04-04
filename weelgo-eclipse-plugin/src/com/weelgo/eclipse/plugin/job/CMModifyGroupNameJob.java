package com.weelgo.eclipse.plugin.job;

import com.weelgo.chainmapping.core.CMGroup;
import com.weelgo.chainmapping.core.CMModuleService;
import com.weelgo.core.IProgressMonitor;
import com.weelgo.eclipse.plugin.CMEvents;
import com.weelgo.eclipse.plugin.Factory;
import com.weelgo.eclipse.plugin.ImagesFactory;

public class CMModifyGroupNameJob extends CMJob {

	private String newName;

	public static CMModifyGroupNameJob CREATE() {
		return Factory.create(CMModifyGroupNameJob.class);
	}

	public CMModifyGroupNameJob() {
		super("Modify group name", "Modifying group name ...");
	}

	@Override
	public String getUndoRedoLabel() {
		return "Modify group name";
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

		CMGroup nd = getSelectedObject(CMGroup.class);
		CMModuleService ser = getModuleService(nd);
		if (ser != null && nd != null) {
			setModuleUniqueIdentifier(ser.getModuleUniqueIdentifier());
			CMGroup gp = ser.modifyGroupName(newName, nd.getUuid());
			sentEvent(CMEvents.GROUP_NAME_MODIFIED, gp);
		}

	}

	public String getNewName() {
		return newName;
	}

	public void setNewName(String newName) {
		this.newName = newName;
	}

}
