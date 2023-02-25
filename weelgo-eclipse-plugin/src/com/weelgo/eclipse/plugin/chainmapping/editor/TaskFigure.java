package com.weelgo.eclipse.plugin.chainmapping.editor;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RectangleFigure;

public class TaskFigure extends NodeFigure{

	@Override
	public IFigure createMainShape() {		
		return new RectangleFigure();
	}

}
