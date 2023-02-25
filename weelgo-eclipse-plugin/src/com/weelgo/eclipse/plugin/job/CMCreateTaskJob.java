package com.weelgo.eclipse.plugin.job;

import com.weelgo.chainmapping.core.CMGroup;
import com.weelgo.chainmapping.core.CMModuleService;
import com.weelgo.chainmapping.core.CMTask;
import com.weelgo.core.CoreUtils;
import com.weelgo.core.IProgressMonitor;
import com.weelgo.eclipse.plugin.CMEvents;
import com.weelgo.eclipse.plugin.Factory;
import com.weelgo.eclipse.plugin.ImagesFactory;

public class CMCreateTaskJob extends CMJob {

	private String taskName;
	private int positionX;
	private int positionY;

	public CMCreateTaskJob() {
		super("Create task", "Creating task ...");
	}

	@Override
	public String getUndoRedoLabel() {
		return "Create task";
	}

	@Override
	public boolean isUndoRedoJob() {
		return true;
	}

	@Override
	public String getUndoRedoIcon() {
		return ImagesFactory.TASK_ICON;
	}

	public static CMCreateTaskJob CREATE() {
		return Factory.create(CMCreateTaskJob.class);
	}

	@Override
	public boolean canExecuteJob() {
		return getSelectedObject(CMGroup.class) != null;
	}

	@Override
	public void doRun(IProgressMonitor monitor) {

		CMGroup gp = getSelectedObject(CMGroup.class);
		CMModuleService ser = getModuleService(gp);
		if (ser != null && gp != null) {
			if (CoreUtils.isNotNullOrEmpty(taskName) == false) {
				taskName = ser.findNameForNewTask(gp.getUuid());
			}
			CMTask tsk = ser.createTask(taskName, gp.getUuid(), positionX, positionY);
			sentEvent(CMEvents.TASK_CREATED, tsk);
		}
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public int getPositionX() {
		return positionX;
	}

	public void setPositionX(int positionX) {
		this.positionX = positionX;
	}

	public int getPositionY() {
		return positionY;
	}

	public void setPositionY(int positionY) {
		this.positionY = positionY;
	}

}
