package com.weelgo.eclipse.plugin.undoredo;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef.requests.ReconnectRequest;

public class NodeLinkEditPart extends AbstractConnectionEditPart {

	@Override
	protected void createEditPolicies() {
		
	}

	@Override
	protected IFigure createFigure() {
		PolylineConnection conn = new PolylineConnection();
		conn.setLineWidthFloat(1);
//		PolygonDecoration decoration = new PolygonDecoration();
//		decoration.setTemplate(PolygonDecoration.TRIANGLE_TIP);
//		conn.setTargetDecoration(decoration);
		return conn;
	}

}
