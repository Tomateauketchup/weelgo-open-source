package com.weelgo.eclipse.plugin.job;

import com.weelgo.chainmapping.core.CMGroup;
import com.weelgo.chainmapping.core.CMModuleService;
import com.weelgo.core.IProgressMonitor;
import com.weelgo.eclipse.plugin.CMEvents;
import com.weelgo.eclipse.plugin.Factory;
import com.weelgo.eclipse.plugin.ImagesFactory;

public class CMModifyGroupBackgroundVisibleJob extends CMJob {

	private boolean visible;

	public static CMModifyGroupBackgroundVisibleJob CREATE() {
		return Factory.create(CMModifyGroupBackgroundVisibleJob.class);
	}

	public CMModifyGroupBackgroundVisibleJob() {
		super("Modify group background visibility", "Modifying group background visibility ...");
	}

	@Override
	public String getUndoRedoLabel() {
		return "Modify group background visibility";
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
			CMGroup gp = ser.modifyGroupBackgroundVisible(visible, nd.getUuid());
			sentEvent(CMEvents.GROUP_BACKGROUND_VISIBLE_MODIFIED, gp);
		}

	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

}
