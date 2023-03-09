package com.weelgo.eclipse.plugin.chainmapping.editor;

import org.eclipse.gef.tools.ConnectionCreationTool;
import org.eclipse.swt.events.KeyEvent;

public class LinkTool extends ConnectionCreationTool {

	public LinkTool() {
		

	}
	
	@Override
	protected boolean handleKeyDown(KeyEvent event) {
		if (event.keyCode == 's') {
			getDomain().loadDefaultTool();
			return true;
		}
		return super.handleKeyDown(event);
	}

}
