package com.weelgo.eclipse.plugin.chainmapping.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;

import com.weelgo.chainmapping.core.CMModuleService;
import com.weelgo.chainmapping.core.CMNode;
import com.weelgo.core.Bound;
import com.weelgo.core.CoreUtils;
import com.weelgo.eclipse.plugin.Factory;

public class CMEditorEditPart extends CMGenericEditPart {

	private CornerModel bottomLeftCorner = new CornerModel(CornerModel.BOTTOM_LEFT);
	private CornerModel bottomRightCorner = new CornerModel(CornerModel.BOTTOM_RIGHT);
	private CornerModel topLeftCorner = new CornerModel(CornerModel.TOP_LEFT);
	private CornerModel topRightCorner = new CornerModel(CornerModel.TOP_RIGHT);
	private double zoom = 1.0;
	private boolean firstOpen = true;

	@Override
	protected IFigure createFigure() {
		FreeformLayer layer = new FreeformLayer();
		layer.setLayoutManager(new FreeformLayout());
		return layer;
	}

	@Override
	protected List getModelChildren() {
		List arl = getNodes();
		if (arl == null) {
			arl = new ArrayList<>();
		}
		arl.add(bottomLeftCorner);
		arl.add(bottomRightCorner);
		arl.add(topLeftCorner);
		arl.add(topRightCorner);
		return arl;
	}

	@Override
	public void refresh() {
		super.refresh();
		if (firstOpen) {
			calculateAndrefreshCorners();
		}
		firstOpen = false;
	}

	public double getZoom() {
		return zoom;
	}

	public void setZoom(double zoom) {
		this.zoom = zoom;
	}

	public List<CMNode> getNodes() {
		List<CMNode> arl = new ArrayList<>();
		CMModuleService ser = getModuleServiceModel();
		if (ser != null) {
			CoreUtils.putListIntoList(ser.getTasks(), arl);
			CoreUtils.putListIntoList(ser.getNeeds(), arl);
		}
		return arl;
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new AppEditLayoutPolicy());
	}

	public CMModuleService getModuleServiceModel() {
		return (CMModuleService) getModel();
	}

	public void calculateAndrefreshCorners() {
		calculateCorners();
		List lst = getChildren();
		if (lst != null) {
			for (Object o : lst) {
				if (o != null && o instanceof CornerEditPart c) {
					c.refreshVisuals();
				}
			}
		}
	}

	public Viewport getViewport() {
		ScalableFreeformRootEditPart parent = (ScalableFreeformRootEditPart) getParent();
		return (Viewport) parent.getFigure();
	}

	public void zoomCHanged() {
		calculateAndrefreshCorners();
	}

	public Point getCursorPosition() {
		org.eclipse.swt.graphics.Point pCurstor = Factory.getCursorPosition(getViewer().getControl());
		Point curstorPosition = new Point(pCurstor.x, pCurstor.y);
		Point realCursorPosition = curstorPosition.scale((double) 1 / zoom);
		return realCursorPosition;
	}

	public void calculateCorners() {

		Viewport viewPort = getViewport();
		Rectangle screen = viewPort.getClientArea();
		Point center = viewPort.getClientArea().getCenter();

		int halfHeight = Math.round((1f / (float) zoom) * (float) screen.height / 2f);
		int halfWidth = Math.round((1f / (float) zoom) * (float) screen.width / 2f);
		int top = center.y - halfHeight;
		int bottom = center.y + halfHeight;
		int left = center.x - halfWidth;
		int right = center.x + halfWidth;
		Bound screenBound = new Bound();
		screenBound.setTop(top);
		screenBound.setBottom(bottom);
		screenBound.setLeft(left);
		screenBound.setRight(right);

		Bound bound = null;
		List<Bound> bnds = new ArrayList<Bound>();
		bnds.add(screenBound);
		List<CMNode> nodes = getNodes();
		if (nodes != null) {

			for (CMNode n : nodes) {
				if (n != null) {
					Bound b = new Bound();
					b.setLeft(n.getPositionX());
					b.setRight(n.getPositionX());
					b.setTop(n.getPositionY());
					b.setBottom(n.getPositionY());
					bnds.add(b);
				}
			}
		}

		bound = Bound.calculateBound(bnds, false, true);

		top = 0;
		bottom = 0;
		left = 0;
		right = 0;

		if (bound != null) {
			top = bound.getTop() - 1000;
			bottom = bound.getBottom() + 1000;
			left = bound.getLeft() - 1000;
			right = bound.getRight() + 1000;

//			top = bound.getTop();
//			bottom = bound.getBottom();
//			left = bound.getLeft();
//			right = bound.getRight();
		}

		int oldTop = topLeftCorner.getPositionY();
		int oldBottom = bottomLeftCorner.getPositionY();

		int oldLeft = topLeftCorner.getPositionX();
		int oldRight = topRightCorner.getPositionX();

		if (oldTop < top) {
			top = oldTop;
		}
		if (oldBottom > bottom) {
			bottom = oldBottom;
		}
		if (oldLeft < left) {
			left = oldLeft;
		}
		if (oldRight > right) {
			right = oldRight;
		}

		bottomLeftCorner.setPositionX(left);
		bottomLeftCorner.setPositionY(bottom);

		bottomRightCorner.setPositionX(right);
		bottomRightCorner.setPositionY(bottom);

		topLeftCorner.setPositionX(left);
		topLeftCorner.setPositionY(top);

		topRightCorner.setPositionX(right);
		topRightCorner.setPositionY(top);

	}

	public Map<String, NodeEditPart> getNodeParts() {
		Map<String, NodeEditPart> map = new HashMap<String, NodeEditPart>();
		List childs = getChildren();
		if (childs != null) {
			for (Object c : childs) {
				if (c != null && c instanceof NodeEditPart) {
					NodeEditPart p = (NodeEditPart) c;
					CMNode m = p.getModelNode();
					if (m != null) {
						map.put(m.getUuid(), p);
					}
				}
			}
		}
		return map;
	}
}
