package com.weelgo.eclipse.plugin.undoredo;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

public class UndoRedoEditPartFactory implements EditPartFactory {

	@Override
	public EditPart createEditPart(EditPart context, Object model) {

		AbstractGraphicalEditPart part = null;

		if (model instanceof NodeModel) {
			part = new NodeEditPart();
		} else if (model instanceof UndoRedoModel) {
			part = new UndoRedoEditPart();
		}else if (model instanceof NodeLinkModel) {
			part = new NodeLinkEditPart();
		}

		part.setModel(model);
		return part;
	}

}
