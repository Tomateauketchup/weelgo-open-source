package com.weelgo.eclipse.plugin.job;

import com.weelgo.chainmapping.core.CMGroup;
import com.weelgo.chainmapping.core.CMModuleService;
import com.weelgo.core.IProgressMonitor;
import com.weelgo.eclipse.plugin.CMEvents;
import com.weelgo.eclipse.plugin.Factory;
import com.weelgo.eclipse.plugin.ImagesFactory;

public class CMModifyGroupBorderVisibleJob extends CMJob {

	private boolean visible;

	public static CMModifyGroupBorderVisibleJob CREATE() {
		return Factory.create(CMModifyGroupBorderVisibleJob.class);
	}

	public CMModifyGroupBorderVisibleJob() {
		super("Modify group border visibility", "Modifying group border visibility ...");
	}

	@Override
	public String getUndoRedoLabel() {
		return "Modify group border visibility";
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
			CMGroup gp = ser.modifyGroupBorderVisible(visible, nd.getUuid());
			sentEvent(CMEvents.GROUP_BORDER_VISIBLE_MODIFIED, gp);
		}

	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

}
