package com.weelgo.eclipse.plugin.chainmapping.editor;

import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Polygon;
import org.eclipse.draw2d.geometry.PointList;

import com.weelgo.chainmapping.core.CMGroup;
import com.weelgo.core.Bound;
import com.weelgo.eclipse.plugin.ColorFactory;

public class GroupEditPart extends CMGenericEditPart {

	private Polygon polygon;

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

		List<Bound> pts = getModelGroup().getPolygon();
		if (pts != null) {
			for (Bound bound : pts) {
				if (bound != null) {
					points.addPoint(bound.getX(), bound.getY());
				}
			}
		}
		polygon.setOpaque(true);
		polygon.setForegroundColor(ColorFactory.BLACK_COLOR);
		polygon.setBackgroundColor(ColorFactory.DEFAULT_GROUP_COLOR);
		polygon.setFill(true);
		polygon.setPoints(points);

	}

	public CMGroup getModelGroup() {
		return (CMGroup) getModel();
	}

}
