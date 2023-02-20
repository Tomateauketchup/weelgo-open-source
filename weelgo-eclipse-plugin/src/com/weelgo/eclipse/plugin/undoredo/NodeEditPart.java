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

public class NodeEditPart extends AbstractGraphicalEditPart
		implements org.eclipse.gef.NodeEditPart, IModuleUniqueIdentifierObject {

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

		int imageSize = NodeFigure.ICON_SIZE;
		int margin = NodeFigure.ICON_LABEL_SPACE;
		int nodeX = model.getPosX();
		int nodeY = model.getPosY();

		int nameWidth = NodeFigure.CURRENT_SELECTION_SIZE;
		int nameHeight = NodeFigure.CURRENT_SELECTION_SIZE;
		int nameX = imageSize + margin;
		int nameY = 0;

		int figureX = nodeX;
		int figureY = nodeY;
		int width = imageSize + margin + nameWidth;
		int height = nameHeight > imageSize ? nameHeight : imageSize;

		nodeFigure.setConstraint(nodeFigure.getIcon(), new Rectangle(0, 0, imageSize, imageSize));
		nodeFigure.setConstraint(nodeFigure.getCurrentSelection(), new Rectangle(nameX, nameY, nameWidth, nameHeight));

		Rectangle layout = new Rectangle(figureX, figureY, width, height);
		parent.setLayoutConstraint(this, nodeFigure, layout);

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

}
