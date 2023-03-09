package com.weelgo.eclipse.plugin.chainmapping.editor;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;

import com.weelgo.eclipse.plugin.ColorFactory;

public abstract class NodeFigure extends Figure {

	public static final int NODE_SIZE = 10;
	public static final int LABEL_H_MARGIN = 10;
	public static final int LABEL_V_MARGIN = 5;

	private IFigure mainShape;
	private Label labelName;
	private ConnectionAnchor connectionAnchor;

	public NodeFigure() {
		setLayoutManager(new XYLayout());
		mainShape = createMainShape();
		add(mainShape);
		labelName = new Label();
		labelName.setForegroundColor(ColorFactory.BLACK_COLOR);
		add(labelName);
	}

	public abstract IFigure createMainShape();

	public IFigure getMainShape() {
		return mainShape;
	}

	public void setMainShape(IFigure mainShape) {
		this.mainShape = mainShape;
	}

	public Label getLabelName() {
		return labelName;
	}

	public void setLabelName(Label labelName) {
		this.labelName = labelName;
	}

	public ConnectionAnchor getConnectionAnchor() {
		if (connectionAnchor == null) {
			connectionAnchor = new ChopboxAnchor(this) {
				@Override
				protected Rectangle getBox() {

					Rectangle box = mainShape.getBounds().getCopy();
					return box;
				}
			};

		}
		return connectionAnchor;
	}

}
