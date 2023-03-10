package com.weelgo.eclipse.plugin.job;

import java.util.List;

import org.eclipse.e4.core.di.annotations.Execute;

import com.weelgo.chainmapping.core.CMGroup;
import com.weelgo.chainmapping.core.CMModuleService;
import com.weelgo.chainmapping.core.CMNode;
import com.weelgo.core.CoreUtils;
import com.weelgo.core.IProgressMonitor;
import com.weelgo.core.IUuidObject;
import com.weelgo.eclipse.plugin.CMEvents;
import com.weelgo.eclipse.plugin.CurrentSelectionService;
import com.weelgo.eclipse.plugin.Factory;
import com.weelgo.eclipse.plugin.ImagesFactory;

public class CMMoveElementsIntoGroupJob extends CMJob {

	private CMGroup parentGroup;

	@Override
	public boolean isUndoRedoJob() {
		return true;
	}

	public CMMoveElementsIntoGroupJob() {
		super("Move into group", "%oving into group ...");
	}

	public static CMMoveElementsIntoGroupJob CREATE() {
		return Factory.create(CMMoveElementsIntoGroupJob.class);
	}

	@Override
	public String getUndoRedoLabel() {
		return "Move into group";
	}

	@Override
	public String getUndoRedoIcon() {
		return ImagesFactory.GROUP_ICON;
	}

	@Override
	public boolean canExecuteJob() {
		return getSelectedObject() != null && getParentGroup() != null;
	}

	@Override
	public void doRun(IProgressMonitor monitor) {

		List sel = getSelectedObject(List.class);
		CMGroup parent = getParentGroup();
		if (parent != null) {
			CMModuleService ser = getModuleService(parent);
			if (ser != null) {
				setModuleUniqueIdentifier(ser.getModuleUniqueIdentifier());
				ser.moveElementsIntoGroup(parent.getUuid(), CoreUtils.transformListToStringArray(sel));
				sentEvent(CMEvents.ELEMENTS_MOVED_INTO_GROUP, parent);
			}
		}
	}

	public CMGroup getParentGroup() {
		return parentGroup;
	}

	public void setParentGroup(CMGroup parentGroup) {
		this.parentGroup = parentGroup;
	}

	@Execute
	public void execute(CurrentSelectionService currentSelectionService) {

		CMGroup gp = currentSelectionService.find(CMGroup.class);
		if (gp != null) {
			setParentGroup(gp);
			setSelectedObject(currentSelectionService
					.findSelectedElementsIntoChainMappingEditor(gp.getModuleUniqueIdentifier(), CMNode.class));
			doSchedule();
		}

	}

}
