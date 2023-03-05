package com.weelgo.eclipse.plugin.chainmapping.editor.views;

import com.weelgo.chainmapping.core.CMTask;
import com.weelgo.eclipse.plugin.selectionViewer.PurePropertiesViewer;

public class TaskView extends PurePropertiesViewer<CMTask> {

	public TaskView() {
		addProperty(new TaskNameUiProperty());
		addProperty(new NamePositionUiProperty());
	}

}
