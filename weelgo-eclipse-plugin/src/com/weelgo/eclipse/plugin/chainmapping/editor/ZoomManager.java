package com.weelgo.eclipse.plugin.chainmapping.editor;

import org.eclipse.draw2d.ScalableFigure;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.geometry.Point;

public class ZoomManager extends org.eclipse.gef.editparts.ZoomManager{

	public ZoomManager(ScalableFigure pane, Viewport viewport) {
		super(pane, viewport);
	}
	
	
	@Override
	protected void primSetZoom(double zoom) {
		super.primSetZoom(zoom);
		
//		getScalableFigure().setScale(zoom);
//		fireZoomChanged();
//		getViewport().validate();
//		setViewLocation(new Point(400,400));
//		super.primSetZoom(zoom);
	}
	

}