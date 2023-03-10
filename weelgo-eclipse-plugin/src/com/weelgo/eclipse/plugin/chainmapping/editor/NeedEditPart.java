package com.weelgo.eclipse.plugin.chainmapping.editor;

import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.graphics.Color;

import com.weelgo.eclipse.plugin.ColorFactory;

public class NeedEditPart extends NodeEditPart {

	@Override
	protected IFigure createFigure() {
		return new NeedFigure();
	}

	@Override
	public Color getNodeBackgroundColo() {
		return ColorFactory.NEED_COLOR;
	}

}
