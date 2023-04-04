package com.weelgo.eclipse.plugin.job;

import com.weelgo.chainmapping.core.CMGroup;
import com.weelgo.chainmapping.core.CMModuleService;
import com.weelgo.core.IProgressMonitor;
import com.weelgo.eclipse.plugin.CMEvents;
import com.weelgo.eclipse.plugin.Factory;
import com.weelgo.eclipse.plugin.ImagesFactory;

public class CMModifyGroupPackageNameJob extends CMJob {

	private String newName;

	public static CMModifyGroupPackageNameJob CREATE() {
		return Factory.create(CMModifyGroupPackageNameJob.class);
	}

	public CMModifyGroupPackageNameJob() {
		super("Modify group package name", "Modifying group package name ...");
	}

	@Override
	public String getUndoRedoLabel() {
		return "Modify group package name";
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
			CMGroup gp = ser.modifyGroupPackageName(newName, nd.getUuid());
			sentEvent(CMEvents.GROUP_PACKAGE_NAME_MODIFIED, gp);
		}

	}

	public String getNewName() {
		return newName;
	}

	public void setNewName(String newName) {
		this.newName = newName;
	}

}
