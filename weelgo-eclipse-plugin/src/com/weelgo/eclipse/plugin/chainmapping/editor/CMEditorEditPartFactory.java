package com.weelgo.eclipse.plugin.chainmapping.editor;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import com.weelgo.chainmapping.core.CMGroup;
import com.weelgo.chainmapping.core.CMLink;
import com.weelgo.chainmapping.core.CMModuleService;
import com.weelgo.chainmapping.core.CMNeed;
import com.weelgo.chainmapping.core.CMTask;
import com.weelgo.eclipse.plugin.Factory;

public class CMEditorEditPartFactory implements EditPartFactory {

	public static CMEditorEditPartFactory CREATE() {
		return Factory.create(CMEditorEditPartFactory.class);
	}

	@Override
	public EditPart createEditPart(EditPart context, Object model) {

		AbstractGraphicalEditPart part = null;
		if (model instanceof CMModuleService) {
			part = Factory.create(CMEditorEditPart.class);
		} else if (model instanceof CMTask) {
			part = Factory.create(TaskEditPart.class);
		} else if (model instanceof CMNeed) {
			part = Factory.create(NeedEditPart.class);
		} else if (model instanceof CornerModel) {
			part = Factory.create(CornerEditPart.class);
		} else if (model instanceof CMLink) {
			part = Factory.create(LinkEditPart.class);
		} else if (model instanceof CMGroup) {
			part = Factory.create(GroupEditPart.class);
		}

		part.setModel(model);
		return part;
	}

}
