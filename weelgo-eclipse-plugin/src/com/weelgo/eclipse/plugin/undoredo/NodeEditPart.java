package com.weelgo.eclipse.plugin.undoredo;

import java.util.List;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import com.weelgo.chainmapping.core.IModuleUniqueIdentifierObject;
import com.weelgo.core.CoreUtils;
import com.weelgo.core.clock.DateUtils;
import com.weelgo.eclipse.plugin.Factory;

public class NodeEditPart extends AbstractGraphicalEditPart
		implements org.eclipse.gef.NodeEditPart, IModuleUniqueIdentifierObject {

	private boolean mouseOver = false;

	@Override
	protected IFigure createFigure() {
		return new NodeFigure();
	}

	@Override
	protected void createEditPolicies() {
	}

	@Override
	protected void refreshVisuals() {

		NodeFigure nodeFigure = getNodeFigure();
		NodeModel model = getNodeModel();
		UndoRedoEditPart parent = (UndoRedoEditPart) getParent();

		nodeFigure.setIsCurrentNode(model.isCurrentNode());
		nodeFigure.setIconData(model.getIcon());
		nodeFigure.setTime(DateUtils.formatTime(model.getTime(), Factory.getTimeZone()));
		nodeFigure.setAction(model.getLabel());
		nodeFigure.setTargetName(model.getTargetName());

		int iconSize = NodeFigure.ICON_SIZE;
		int margin = NodeFigure.ICON_LABEL_SPACE;
		int nodeX = model.getPosX();
		int nodeY = model.getPosY();

		int mouseOverShapeWidth = NodeFigure.MOUSE_OVER_SHAPE_SIZE;
		int mouseOverShapeHeight = NodeFigure.MOUSE_OVER_SHAPE_SIZE;
		int mouseOverX = 0;
		int mouseOverY = 0;

		int saveIconWidth = NodeFigure.SAVE_ICON_SIZE;
		int saveIconHeight = NodeFigure.SAVE_ICON_SIZE;
		int saveIconX = 0;
		int saveIconY = Math.round((float) (mouseOverShapeHeight - saveIconHeight) / (float) 2);
		;

		int offsetX = saveIconWidth;

		mouseOverX = offsetX;

		int iconWidth = iconSize;
		int iconHeight = iconSize;
		int iconX = offsetX + Math.round((float) (mouseOverShapeWidth - iconWidth) / (float) 2);
		int iconY = Math.round((float) (mouseOverShapeHeight - iconHeight) / (float) 2);

		int currentUndoRedoWidth = NodeFigure.CURRENT_UNDO_REDO_SELECTION_SIZE;
		int currentUndoRedoHeight = NodeFigure.CURRENT_UNDO_REDO_SELECTION_SIZE;
		int currentUndoRedoX = iconX + iconWidth + margin;
		int currentUndoRedoY = Math.round((float) (mouseOverShapeHeight - currentUndoRedoHeight) / (float) 2);
		;

		int figureX = nodeX;
		int figureY = nodeY;
		int width = iconX + iconWidth + margin + currentUndoRedoWidth;
		int height = mouseOverShapeHeight;

		nodeFigure.setConstraint(nodeFigure.getSaveIcon(),
				new Rectangle(saveIconX, saveIconY, saveIconWidth, saveIconHeight));
		nodeFigure.setConstraint(nodeFigure.getMouseOverShape(),
				new Rectangle(mouseOverX, mouseOverY, mouseOverShapeWidth, mouseOverShapeHeight));
		nodeFigure.setConstraint(nodeFigure.getIcon(), new Rectangle(iconX, iconY, iconWidth, iconHeight));
		nodeFigure.setConstraint(nodeFigure.getCurrentSelection(),
				new Rectangle(currentUndoRedoX, currentUndoRedoY, currentUndoRedoWidth, currentUndoRedoHeight));

		Rectangle layout = new Rectangle(figureX, figureY, width, height);
		if (parent != null) {
			parent.setLayoutConstraint(this, nodeFigure, layout);
		}

		nodeFigure.setIsSaved(model.isSavedNode());
		nodeFigure.getMouseOverShape().setVisible(isMouseOver());

	}

	public NodeFigure getNodeFigure() {
		return (NodeFigure) getFigure();
	}

	public NodeModel getNodeModel() {
		return (NodeModel) getModel();
	}

	@Override
	protected List<NodeLinkModel> getModelSourceConnections() {
		return getNodeModel().getChilds();
	}

	@Override
	protected List<NodeLinkModel> getModelTargetConnections() {
		return CoreUtils.putObjectIntoList(getNodeModel().getParent());
	}

	@Override
	public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart arg0) {
		return ((NodeFigure) getFigure()).getConnectionAnchor();
	}

	@Override
	public ConnectionAnchor getSourceConnectionAnchor(Request arg0) {
		return ((NodeFigure) getFigure()).getConnectionAnchor();
	}

	@Override
	public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart arg0) {
		return ((NodeFigure) getFigure()).getConnectionAnchor();
	}

	@Override
	public ConnectionAnchor getTargetConnectionAnchor(Request arg0) {
		return ((NodeFigure) getFigure()).getConnectionAnchor();
	}

	public UndoRedoEditPart getUndoRedoEditPartParent() {
		return (UndoRedoEditPart) getParent();
	}

	@Override
	public String getModuleUniqueIdentifier() {
		UndoRedoEditPart pt = getUndoRedoEditPartParent();
		if (pt != null) {
			return pt.getModuleUniqueIdentifier();
		}
		return "";
	}

	@Override
	public void setModuleUniqueIdentifier(String arg0) {

	}

	public boolean isMouseOver() {
		return mouseOver;
	}

	public void setMouseOver(boolean mouseOver) {
		this.mouseOver = mouseOver;
	}
}
