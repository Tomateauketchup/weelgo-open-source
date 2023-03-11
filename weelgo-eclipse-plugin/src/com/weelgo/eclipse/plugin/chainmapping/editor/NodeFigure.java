package com.weelgo.eclipse.plugin.chainmapping.editor;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;

import com.weelgo.eclipse.plugin.ColorFactory;

public abstract class NodeFigure extends Figure {

	public static final int NODE_SIZE = 10;
	public static final int LABEL_H_MARGIN = 5;
	public static final int LABEL_V_MARGIN = 5;

	private IFigure mainShape;
	private Label labelName;
	private RectangleFigure labelBackground;
	private ConnectionAnchor connectionAnchor;

	public NodeFigure() {
		setLayoutManager(new XYLayout());
		mainShape = createMainShape();
		add(mainShape);
		if (showLabelBackground()) {
			labelBackground = new RectangleFigure();
			labelBackground.setBackgroundColor(ColorFactory.WHITE_COLOR);
			labelBackground.setForegroundColor(ColorFactory.WHITE_COLOR);
			add(labelBackground);
		}
		labelName = new Label();
		labelName.setForegroundColor(ColorFactory.BLACK_COLOR);
		add(labelName);
	}

	public boolean showLabelBackground() {
		return false;
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

	public RectangleFigure getLabelBackground() {
		return labelBackground;
	}

	public void setLabelBackground(RectangleFigure labelBackground) {
		this.labelBackground = labelBackground;
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
