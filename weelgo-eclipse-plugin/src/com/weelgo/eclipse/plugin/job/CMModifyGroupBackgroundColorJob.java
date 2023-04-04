package com.weelgo.eclipse.plugin.job;

import com.weelgo.chainmapping.core.CMGroup;
import com.weelgo.chainmapping.core.CMModuleService;
import com.weelgo.core.Color;
import com.weelgo.core.IProgressMonitor;
import com.weelgo.eclipse.plugin.CMEvents;
import com.weelgo.eclipse.plugin.Factory;
import com.weelgo.eclipse.plugin.ImagesFactory;

public class CMModifyGroupBackgroundColorJob extends CMJob {

	private Color color;

	public static CMModifyGroupBackgroundColorJob CREATE() {
		return Factory.create(CMModifyGroupBackgroundColorJob.class);
	}

	public CMModifyGroupBackgroundColorJob() {
		super("Modify group background color", "Modifying group background color ...");
	}

	@Override
	public String getUndoRedoLabel() {
		return "Modify group background color";
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
			CMGroup gp = ser.modifyGroupBackgroundColor(color, nd.getUuid());
			sentEvent(CMEvents.GROUP_BACKGROUND_COLOR_MODIFIED, gp);
		}

	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

}
