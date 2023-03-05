package com.weelgo.eclipse.plugin.chainmapping.editor;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPolicy;

import com.weelgo.chainmapping.core.CMNode;
import com.weelgo.eclipse.plugin.ColorFactory;

public abstract class NodeEditPart extends CMGenericEditPart {

	@Override
	protected void refreshVisuals() {
		super.refreshVisuals();
		NodeFigure nodeFigure = (NodeFigure) getFigure();

		CMEditorEditPart parent = (CMEditorEditPart) getParent();

		ShapesPositions p = calculatePositions();

		nodeFigure.setConstraint(nodeFigure.getMainShape(),
				new Rectangle(p.shapeX, p.shapeY, p.shapeWidth, p.shapeHeight));

		nodeFigure.setConstraint(nodeFigure.getLabelName(), new Rectangle(p.nameX, p.nameY, p.nameWidth, p.nameHeight));

		Rectangle layout = new Rectangle(p.figureX, p.figureY, p.figureWidth, p.figureHeight);
		parent.setLayoutConstraint(this, nodeFigure, layout);

		nodeFigure.getMainShape().setBackgroundColor(ColorFactory.GREY_COLOR);
//		System.out.println("x:" + p.figureX + ", y:" + p.figureY);

	}

	public ShapesPositions calculatePositions() {
		ShapesPositions p = new ShapesPositions();

		CMNode node = (CMNode) getModel();
		NodeFigure nodeFigure = (NodeFigure) getFigure();

		nodeFigure.getLabelName().setText(node.getName());
		Rectangle textBounds = nodeFigure.getLabelName().getTextBounds();

		String namePosition = node.getNamePosition();

		int nodeX = node.getPositionX();
		int nodeY = node.getPositionY();

		int figureHeight = 0;
		int figureWidth = 0;
		int figureX = nodeX;
		int figureY = nodeY;

		int shapeHeight = NodeFigure.NODE_SIZE;
		int shapeWidth = NodeFigure.NODE_SIZE;
		int shapeX = 0;
		int shapeY = 0;

		int nameHeight = textBounds.height;
		int nameWidth = textBounds.width;
		int nameX = 0;
		int nameY = 0;

		if (CMNode.NAME_TOP.equals(namePosition)) {
			figureHeight = nameHeight + NodeFigure.LABEL_V_MARGIN + shapeHeight;
			figureWidth = nameWidth > shapeWidth ? nameWidth : shapeWidth;

			shapeX = Math.round((float) (figureWidth - shapeWidth) / (float) 2);
			shapeY = figureHeight - shapeHeight;

			nameX = Math.round((float) (figureWidth - nameWidth) / (float) 2);
			nameY = 0;

			figureX = figureX - Math.round((float) figureWidth / (float) 2);
			figureY = figureY - figureHeight + Math.round((float) shapeHeight / (float) 2);
		} else if (CMNode.NAME_BOTTOM.equals(namePosition)) {
			figureHeight = nameHeight + NodeFigure.LABEL_V_MARGIN + shapeHeight;
			figureWidth = nameWidth > shapeWidth ? nameWidth : shapeWidth;

			shapeX = Math.round((float) (figureWidth - shapeWidth) / (float) 2);
			shapeY = 0;

			nameX = Math.round((float) (figureWidth - nameWidth) / (float) 2);
			nameY = figureHeight - nameHeight;

			figureX = figureX - Math.round((float) figureWidth / (float) 2);
			figureY = figureY - Math.round((float) shapeHeight / (float) 2);
		} else if (CMNode.NAME_LEFT.equals(namePosition)) {
			figureHeight = nameHeight > shapeHeight ? nameHeight : shapeHeight;
			figureWidth = nameWidth + NodeFigure.LABEL_H_MARGIN + shapeWidth;

			shapeX = nameWidth + NodeFigure.LABEL_H_MARGIN;
			shapeY = Math.round((float) (figureHeight - shapeHeight) / (float) 2);

			nameX = 0;
			nameY = Math.round((float) (figureHeight - nameHeight) / (float) 2);

			figureX = figureX - nameWidth - NodeFigure.LABEL_H_MARGIN - Math.round((float) shapeWidth / (float) 2);
			figureY = figureY - Math.round((float) figureHeight / (float) 2);
		} else if (CMNode.NAME_RIGHT.equals(namePosition)) {
			figureHeight = nameHeight > shapeHeight ? nameHeight : shapeHeight;
			figureWidth = nameWidth + NodeFigure.LABEL_H_MARGIN + shapeWidth;

			shapeX = 0;
			shapeY = Math.round((float) (figureHeight - shapeHeight) / (float) 2);

			nameX = shapeWidth + NodeFigure.LABEL_H_MARGIN;
			nameY = Math.round((float) (figureHeight - nameHeight) / (float) 2);

			figureX = figureX - Math.round((float) shapeWidth / (float) 2);
			figureY = figureY - Math.round((float) figureHeight / (float) 2);
		}

		p.nodeX = nodeX;
		p.nodeY = nodeY;

		p.figureHeight = figureHeight;
		p.figureWidth = figureWidth;
		p.figureX = figureX;
		p.figureY = figureY;

		p.shapeHeight = shapeHeight;
		p.shapeWidth = shapeWidth;
		p.shapeX = shapeX;
		p.shapeY = shapeY;

		p.nameHeight = nameHeight;
		p.nameWidth = nameWidth;
		p.nameX = nameX;
		p.nameY = nameY;

		return p;

	}

	public void recalculateMousePosition(Rectangle rect) {
		ShapesPositions p = calculatePositions();

		int deltaX = p.figureX - p.nodeX;
		rect.x = rect.x - deltaX;

		int deltaY = p.figureY - p.nodeY;
		rect.y = rect.y - deltaY;
	}

	@Override
	protected void createEditPolicies() {

	}

	public NodeFigure getNodeFigure() {
		return (NodeFigure) getFigure();
	}

	public CMNode getModelNode() {
		return (CMNode) getModel();
	}

	public class ShapesPositions {
		private int nodeX = 0;
		private int nodeY = 0;

		private int figureHeight = 0;
		private int figureWidth = 0;
		private int figureX = 0;
		private int figureY = 0;

		private int shapeHeight = NodeFigure.NODE_SIZE;
		private int shapeWidth = NodeFigure.NODE_SIZE;
		private int shapeX = 0;
		private int shapeY = 0;

		private int nameHeight = 0;
		private int nameWidth = 0;
		private int nameX = 0;
		private int nameY = 0;

	}

	// TODO quand on change de la vue chain mapping à une autre vue et qu'on
	// revient, on pred le CTRL S

}
