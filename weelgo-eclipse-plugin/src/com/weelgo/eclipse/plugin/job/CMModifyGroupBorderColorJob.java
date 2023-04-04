package com.weelgo.eclipse.plugin.job;

import com.weelgo.chainmapping.core.CMGroup;
import com.weelgo.chainmapping.core.CMModuleService;
import com.weelgo.core.Color;
import com.weelgo.core.IProgressMonitor;
import com.weelgo.eclipse.plugin.CMEvents;
import com.weelgo.eclipse.plugin.Factory;
import com.weelgo.eclipse.plugin.ImagesFactory;

public class CMModifyGroupBorderColorJob extends CMJob {

	private Color color;

	public static CMModifyGroupBorderColorJob CREATE() {
		return Factory.create(CMModifyGroupBorderColorJob.class);
	}

	public CMModifyGroupBorderColorJob() {
		super("Modify group border color", "Modifying group border color ...");
	}

	@Override
	public String getUndoRedoLabel() {
		return "Modify group border color";
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
			CMGroup gp = ser.modifyGroupBorderColor(color, nd.getUuid());
			sentEvent(CMEvents.GROUP_BORDER_COLOR_MODIFIED, gp);
		}

	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

}
