package com.weelgo.eclipse.plugin.job;

import com.weelgo.chainmapping.core.CMModuleService;
import com.weelgo.chainmapping.core.CMTask;
import com.weelgo.core.IProgressMonitor;
import com.weelgo.eclipse.plugin.CMEvents;
import com.weelgo.eclipse.plugin.Factory;
import com.weelgo.eclipse.plugin.ImagesFactory;

public class CMMoveTaskJob extends CMJob {

	private int positionX;
	private int positionY;

	public CMMoveTaskJob() {
		super("Move task", "Moving task ...");
	}

	@Override
	public String getUndoRedoLabel() {
		return "Move task";
	}

	@Override
	public boolean isUndoRedoJob() {
		return true;
	}

	@Override
	public String getUndoRedoIcon() {
		return ImagesFactory.SOURCE;
	}

	public static CMMoveTaskJob CREATE() {
		return Factory.create(CMMoveTaskJob.class);
	}

	@Override
	public boolean canExecuteJob() {
		return getSelectedObject(CMTask.class) != null;
	}

	@Override
	public void doRun(IProgressMonitor monitor) {
		CMTask tsk = getSelectedObject(CMTask.class);
		if (tsk != null) {			
			CMModuleService ser = getModuleService(tsk);
			if (ser != null) {
				setModuleUniqueIdentifier(ser.getModuleUniqueIdentifier());
				tsk = ser.moveNode(tsk.getUuid(), positionX, positionY);
				sentEvent(CMEvents.TASK_POSITION_CHANGED, tsk);
			}
		}
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
