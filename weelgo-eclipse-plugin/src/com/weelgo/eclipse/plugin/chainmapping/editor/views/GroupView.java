package com.weelgo.eclipse.plugin.chainmapping.editor.views;

import com.weelgo.chainmapping.core.CMTask;
import com.weelgo.eclipse.plugin.selectionViewer.PurePropertiesViewer;

public class GroupView extends PurePropertiesViewer<CMTask> {

	public GroupView() {
		addProperty(new GroupNameUiProperty());
		addProperty(new GroupPackageNameUiProperty());
		addProperty(new GroupBackgroundVisibleUiProperty());
		addProperty(new GroupBackgroundColorUiProperty());
		addProperty(new GroupBorderVisibleUiProperty());
		addProperty(new GroupBorderColorUiProperty());
	}

}
