package com.weelgo.eclipse.plugin.job;

import com.weelgo.chainmapping.core.CMGroup;
import com.weelgo.chainmapping.core.CMModuleService;
import com.weelgo.chainmapping.core.CMNeed;
import com.weelgo.chainmapping.core.CMTask;
import com.weelgo.core.CoreUtils;
import com.weelgo.core.IProgressMonitor;
import com.weelgo.eclipse.plugin.CMEvents;
import com.weelgo.eclipse.plugin.Factory;
import com.weelgo.eclipse.plugin.ImagesFactory;

public class CMCreateNeedJob extends CMJob {

	private String needName;
	private int positionX;
	private int positionY;

	public CMCreateNeedJob() {
		super("Create need", "Creating need ...");
	}

	@Override
	public String getUndoRedoLabel() {
		return "Create need";
	}

	@Override
	public boolean isUndoRedoJob() {
		return true;
	}

	@Override
	public String getUndoRedoIcon() {
		return ImagesFactory.NEED_ICON;
	}

	public static CMCreateNeedJob CREATE() {
		return Factory.create(CMCreateNeedJob.class);
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
			if (CoreUtils.isNotNullOrEmpty(needName) == false) {
				needName = ser.findNameForNewNeed(gp.getUuid());
			}
			CMNeed tsk = ser.createNeed(needName, gp.getUuid(), positionX, positionY);
			if (tsk != null) {
				setUndoRedoTargetName(tsk.getName());
			}
			sentEvent(CMEvents.NEED_CREATED, tsk);
		}
	}

	public String getNeedName() {
		return needName;
	}

	public void setNeedName(String needName) {
		this.needName = needName;
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
