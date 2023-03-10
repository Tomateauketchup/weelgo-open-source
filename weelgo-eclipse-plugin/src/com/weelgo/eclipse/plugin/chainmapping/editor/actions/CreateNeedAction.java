package com.weelgo.eclipse.plugin.chainmapping.editor.actions;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.ui.IWorkbenchPart;

import com.weelgo.chainmapping.core.CMGroup;
import com.weelgo.eclipse.plugin.ImagesFactory;
import com.weelgo.eclipse.plugin.job.CMCreateNeedJob;

public class CreateNeedAction extends GenericSelectionAction {

	public static String CREATE_NEED = "create_need";

	public CreateNeedAction(IWorkbenchPart part) {
		super(part);
		setImageDescriptor(ImagesFactory.getIconsImageDescriptor(ImagesFactory.NEED_ICON));
		setText("Create need");
		setToolTipText("Create need");
	}

	@Override
	protected void init() {
		super.init();
		setId(CREATE_NEED);
	}

	@Override
	public void run() {
		CMCreateNeedJob j = CMCreateNeedJob.CREATE();
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
