package com.weelgo.eclipse.plugin.undoredo;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.Ellipse;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;

import com.weelgo.eclipse.plugin.ColorFactory;
import com.weelgo.eclipse.plugin.ImagesFactory;

public class NodeFigure extends ImageFigure {

	public static final int ICON_SIZE = ImagesFactory.ICON_SIZE_IN_PX;
	public static final int CURRENT_SELECTION_SIZE = 16;
	public static final int ICON_LABEL_SPACE = 1;

	private ImageFigure currentSelection = new ImageFigure();
	private ImageFigure icon = new ImageFigure();
	private ConnectionAnchor connectionAnchor;

	public NodeFigure() {
		setLayoutManager(new XYLayout());
		add(icon);

//		currentSelectionEllipse.setBackgroundColor(ColorFactory.BLACK_COLOR);
//		currentSelectionEllipse.setForegroundColor(ColorFactory.BLACK_COLOR);
		currentSelection.setImage(ImagesFactory.getIconImage(ImagesFactory.ARROW_LEFT));
		add(currentSelection);
	}

	public void setIsCurrentNode(boolean isCurrentNode) {
		currentSelection.setVisible(isCurrentNode);
	}

	public void setIconData(String iconStr) {
		icon.setImage(ImagesFactory.getIconImage(iconStr));
	}

	public ImageFigure getCurrentSelection() {
		return currentSelection;
	}

	public void setCurrentSelection(ImageFigure currentSelection) {
		this.currentSelection = currentSelection;
	}

	public ImageFigure getIcon() {
		return icon;
	}

	public void setIcon(ImageFigure icon) {
		this.icon = icon;
	}

	public ConnectionAnchor getConnectionAnchor() {
		if (connectionAnchor == null) {
			connectionAnchor = new ChopboxAnchor(this) {
				@Override
				protected Rectangle getBox() {
					Rectangle box = icon.getBounds().getCopy();
					return box;
				}
			};

		}
		return connectionAnchor;
	}

}
