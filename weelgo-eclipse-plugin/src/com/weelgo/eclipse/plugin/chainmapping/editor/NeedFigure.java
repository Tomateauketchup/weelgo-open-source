package com.weelgo.eclipse.plugin.chainmapping.editor;

import org.eclipse.draw2d.Ellipse;
import org.eclipse.draw2d.IFigure;

public class NeedFigure extends NodeFigure{

	@Override
	public IFigure createMainShape() {
		return new Ellipse();
	}

}
