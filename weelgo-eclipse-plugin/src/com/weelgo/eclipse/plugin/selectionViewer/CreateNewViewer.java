 
package com.weelgo.eclipse.plugin.selectionViewer;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;

import com.weelgo.core.CoreUtils;

public class CreateNewViewer {	
	
	@Execute
	public void execute(EPartService partService) {		
		
		MPart part = partService.createPart("com.weelgo.eclipse.plugin.selectionViewer.SelectionViewerPart");
		part.setLabel("Selection viewer");
		part.getPersistedState().put("some key",CoreUtils.generateUUIDString());
		partService.showPart(part, PartState.ACTIVATE);
		
	}
		
}