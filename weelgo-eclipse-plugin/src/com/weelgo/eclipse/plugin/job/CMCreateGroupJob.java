package com.weelgo.eclipse.plugin.job;

import org.eclipse.e4.core.di.annotations.Execute;

import com.weelgo.chainmapping.core.CMGroup;
import com.weelgo.chainmapping.core.CMModuleService;
import com.weelgo.core.IProgressMonitor;
import com.weelgo.eclipse.plugin.CMEvents;
import com.weelgo.eclipse.plugin.CurrentSelectionService;
import com.weelgo.eclipse.plugin.Factory;
import com.weelgo.eclipse.plugin.ImagesFactory;

public class CMCreateGroupJob extends CMJob {

	private Object selectedParent;

	@Override
	public boolean isUndoRedoJob() {
		return true;
	}

	public Object getSelectedParent() {
		return selectedParent;
	}

	public void setSelectedParent(Object selectedParent) {
		this.selectedParent = selectedParent;
	}

	public CMCreateGroupJob() {
		super("Create group", "Creating group ...");
	}

	public static CMCreateGroupJob CREATE() {
		return Factory.create(CMCreateGroupJob.class);
	}

	@Override
	public String getUndoRedoLabel() {
		return "Create group";
	}

	@Override
	public String getUndoRedoIcon() {
		return ImagesFactory.GROUP_ICON;
	}

	@Override
	public void doRun(IProgressMonitor monitor) {

		CMModuleService ser = getModuleService(selectedParent);
		if (ser != null && selectedParent instanceof CMGroup) {
			setModuleUniqueIdentifier(ser.getModuleUniqueIdentifier());
			CMGroup gp = (CMGroup) selectedParent;
			String[] ret = getServices().getModulesManager().findNameForNewGroup(ser, gp.getUuid());
			if (ret != null && ret.length > 1) {
				CMGroup newGp = ser.createGroup(getServices().getModulesManager(), ret[0], ret[1], gp.getUuid());
				sentEvent(CMEvents.GROUP_CREATED, newGp);
			}

		}
	}

	@Execute
	public void execute(CurrentSelectionService currentSelectionService) {

		setSelectedParent(currentSelectionService.getCurrentSelection());
		doSchedule();

	}

}
