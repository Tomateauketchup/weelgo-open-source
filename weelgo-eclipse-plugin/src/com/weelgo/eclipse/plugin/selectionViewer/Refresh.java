
package com.weelgo.eclipse.plugin.selectionViewer;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;

public class Refresh {

	@Execute
	public void execute(MPart part) {

		if (part.getObject() instanceof SelectionViewerPart viewPart) {
			viewPart.refresh();
		}

	}

}