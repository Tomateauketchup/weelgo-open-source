package com.weelgo.eclipse.plugin.chainmapping.editor;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import com.weelgo.eclipse.plugin.ColorFactory;

public class CornerEditPart extends AbstractGraphicalEditPart {

	@Override
	protected IFigure createFigure() {
		RectangleFigure rec = new RectangleFigure();
		rec.setBackgroundColor(ColorFactory.GREEN_COLOR);
		return rec;
	}

	@Override
	protected void refreshVisuals() {

		int size = 10;
		int offset=20;
		CornerModel node = (CornerModel) getModel();
		RectangleFigure nodeFigure = (RectangleFigure) getFigure();

		CMEditorEditPart parent = (CMEditorEditPart) getParent();

		int posX = node.getPositionX();
		int posY = node.getPositionY();

		if (CornerModel.TOP_RIGHT.equals(node.getCorner())) {
			posX = posX - size - offset;
		} else if (CornerModel.BOTTOM_LEFT.equals(node.getCorner())) {
			posY = posY - size - offset;
		} else if (CornerModel.BOTTOM_RIGHT.equals(node.getCorner())) {
			posY = posY - size - offset;
			posX = posX - size - offset;
		}

		Rectangle layout = new Rectangle(posX, posY, size, size);
		parent.setLayoutConstraint(this, nodeFigure, layout);

	}

	@Override
	protected void createEditPolicies() {

	}

	@Override
	public boolean isSelectable() {
		return false;
	}

}
