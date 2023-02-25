package com.weelgo.eclipse.plugin.chainmapping.editor.actions;

import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IWorkbenchPart;

import com.weelgo.chainmapping.core.CMGroup;
import com.weelgo.eclipse.plugin.job.CMCreateTaskJob;

public class CreateTaskAction extends GenericSelectionAction {

	public static String CREATE_TASK = "create_task";

	public CreateTaskAction(IWorkbenchPart part) {
		super(part);
	}

	@Override
	protected void init() {
		super.init();
		setId(CREATE_TASK);
	}

	@Override
	public void run() {
		CMCreateTaskJob j = CMCreateTaskJob.CREATE();
		CMGroup gp = find(CMGroup.class);
		if (gp != null) {
			j.setModuleUniqueIdentifier(gp.getModuleUniqueIdentifier());
		}
		j.setSelectedObject(gp);
		Point point = getCursorPosition();
		if (point != null) {
			j.setPositionX(point.x);
			j.setPositionY(point.y);
		}
		j.doSchedule();
	}

}
