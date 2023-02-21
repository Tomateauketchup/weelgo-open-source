package com.weelgo.eclipse.plugin.undoredo;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.StackLayout;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;

import com.weelgo.core.CoreUtils;
import com.weelgo.eclipse.plugin.ColorFactory;
import com.weelgo.eclipse.plugin.ImagesFactory;

public class NodeFigure extends ImageFigure {

	public static final int ICON_SIZE = ImagesFactory.ICON_SIZE_IN_PX;
	public static final int CURRENT_UNDO_REDO_SELECTION_SIZE = ImagesFactory.ICON_SIZE_IN_PX;
	public static final int SAVE_ICON_SIZE = ImagesFactory.ICON_SIZE_IN_PX;
	public static final int MOUSE_OVER_SHAPE_SIZE = 22;
	public static final int ICON_LABEL_SPACE = 1;

	private ImageFigure saveIcon = new ImageFigure();
	private ImageFigure currentSelection = new ImageFigure();
	private ImageFigure icon = new ImageFigure();
	private ConnectionAnchor connectionAnchor;
	private RectangleFigure mouseOverShape = new RectangleFigure();
	private NodeTooltipFigure nodeTooltip = new NodeTooltipFigure();

	public NodeFigure() {
		setLayoutManager(new XYLayout());

		setToolTip(nodeTooltip);

		mouseOverShape.setBackgroundColor(ColorFactory.MOUSE_OVER_BACKGROUND_COLOR);
		mouseOverShape.setForegroundColor(ColorFactory.MOUSE_OVER_BACKGROUND_COLOR);
		mouseOverShape.setVisible(false);
		add(mouseOverShape);

		saveIcon.setImage(ImagesFactory.getIconImage(ImagesFactory.SAVE_ICON));
		saveIcon.setVisible(false);
		add(saveIcon);
		add(icon);

		currentSelection.setImage(ImagesFactory.getIconImage(ImagesFactory.ARROW_LEFT));
		currentSelection.setVisible(false);
		add(currentSelection);
	}

	public void setIsCurrentNode(boolean isCurrentNode) {
		currentSelection.setVisible(isCurrentNode);
	}
	
	public void setIsSaved(boolean isSaved) {
		saveIcon.setVisible(isSaved);
	}

	public void setTime(String time) {
		nodeTooltip.setTimeString(time);
	}

	public void setAction(String action) {
		nodeTooltip.setActionString(action);
	}

	public void setTargetName(String target) {
		nodeTooltip.setTargetNameString(target);
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

	public ImageFigure getSaveIcon() {
		return saveIcon;
	}

	public void setSaveIcon(ImageFigure saveIcon) {
		this.saveIcon = saveIcon;
	}

	public ImageFigure getIcon() {
		return icon;
	}

	public void setIcon(ImageFigure icon) {
		this.icon = icon;
	}

	public RectangleFigure getMouseOverShape() {
		return mouseOverShape;
	}

	public void setMouseOverShape(RectangleFigure mouseOverShape) {
		this.mouseOverShape = mouseOverShape;
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

	private class NodeTooltipFigure extends RectangleFigure {
		private String timeString;
		private String actionString;
		private String targetNameString;
		private Label timeLabel = new Label();

		public NodeTooltipFigure() {

			setBackgroundColor(ColorFactory.TOOLTIP_BACKGROUND_COLOR);
			setForegroundColor(ColorFactory.TOOLTIP_BORDER_COLOR);
			GridLayout t = new GridLayout(1, false);
			setLayoutManager(t);

			timeLabel.setForegroundColor(ColorFactory.BLACK_COLOR);
			add(timeLabel);

		}

		public void setTimeString(String timeString) {
			this.timeString = timeString;
			refreshVisual();
		}

		public void setActionString(String actionString) {
			this.actionString = actionString;
			refreshVisual();
		}

		public void setTargetNameString(String targetNameString) {
			this.targetNameString = targetNameString;
			refreshVisual();
		}

		public void refreshVisual() {
			timeString = CoreUtils.cleanString(timeString);
			actionString = CoreUtils.cleanString(actionString);
			targetNameString = CoreUtils.cleanString(targetNameString);

			String str = timeString + " : " + actionString;
			if (CoreUtils.isNotNullOrEmpty(targetNameString)) {
				str = str + " -> " + targetNameString;
			}

			timeLabel.setText(str);
		}

	}
}
