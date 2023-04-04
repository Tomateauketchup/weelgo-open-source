package com.weelgo.eclipse.plugin.chainmapping.editor;

import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Polygon;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.swt.graphics.Color;

import com.weelgo.chainmapping.core.CMGroup;
import com.weelgo.core.Bound;
import com.weelgo.eclipse.plugin.ColorFactory;

public class GroupEditPart extends CMGenericEditPart {

	private Polygon polygon;
	private Color backgroundColor = null;
	private Color borderColor = null;

	@Override
	public void disposeObject() {
		ColorFactory.disposeColor(backgroundColor);
		ColorFactory.disposeColor(borderColor);
		super.disposeObject();
	}

	@Override
	protected IFigure createFigure() {
		polygon = new Polygon();
		return polygon;
	}

	@Override
	public boolean isSelectable() {
		return false;
	}

	@Override
	protected void createEditPolicies() {
	}

	@Override
	protected void refreshVisuals() {
		super.refreshVisuals();

		PointList points = new PointList();

		com.weelgo.core.Color bgColorTmp = getModelGroup().getBackgroundColor();
		if (bgColorTmp == null) {
			bgColorTmp = com.weelgo.core.Color.CREATE_DEFAULT_GROUP_BACKGROUND_COLOR();
		}

		com.weelgo.core.Color borderColorTmp = getModelGroup().getBorderColor();
		if (borderColorTmp == null) {
			borderColorTmp = com.weelgo.core.Color.CREATE_DEFAULT_GROUP_BORDER_COLOR();
		}

		ColorFactory.disposeColor(backgroundColor);
		backgroundColor = getColor(bgColorTmp, getModelGroup().isBackgroundVisible(), getParentBackgroundColor());

		ColorFactory.disposeColor(borderColor);
		borderColor = getColor(borderColorTmp, getModelGroup().isBorderVisible(), getParentBackgroundColor());

		List<Bound> pts = getModelGroup().getPolygon();
		if (pts != null) {
			for (Bound bound : pts) {
				if (bound != null) {
					points.addPoint(bound.getX(), bound.getY());
				}
			}
		}
		polygon.setOpaque(true);
		polygon.setForegroundColor(borderColor);
		polygon.setBackgroundColor(backgroundColor);
		polygon.setFill(true);
		polygon.setPoints(points);

	}

	public com.weelgo.core.Color getParentBackgroundColor() {
		CMGroup gp = getModelGroup();
		if (gp != null) {
			CMGroup parentGp = getModuleService().getObjectByUuid(gp.getGroupUuid());
			if (parentGp != null) {
				return parentGp.getBackgroundColor();
			}
		}
		return com.weelgo.core.Color.CREATE_WHITE();
	}

	public Color getColor(com.weelgo.core.Color c, boolean visible, com.weelgo.core.Color parent) {
		if (visible) {
			return ColorFactory.createColor(c);
		} else {
			if (parent != null) {
				return ColorFactory.createColor(parent);
			}
			return ColorFactory.createTransparentColor();
		}
	}

	public CMGroup getModelGroup() {
		return (CMGroup) getModel();
	}

}
