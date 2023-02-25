package com.weelgo.eclipse.plugin.chainmapping.editor;

import org.eclipse.draw2d.IFigure;

public class TaskEditPart extends NodeEditPart{

	@Override
	protected IFigure createFigure() {		
		return new TaskFigure();
	}
	
	
}
