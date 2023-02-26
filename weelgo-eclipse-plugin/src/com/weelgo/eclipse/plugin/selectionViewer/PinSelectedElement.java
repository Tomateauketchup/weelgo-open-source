
package com.weelgo.eclipse.plugin.selectionViewer;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MItem;

public class PinSelectedElement {
	@Execute
	public void execute(MPart part,MItem mitem) {

		if( part.getObject() instanceof SelectionViewerPart viewPart)
		{
			viewPart.pin(mitem.isSelected());
		}



	}

}