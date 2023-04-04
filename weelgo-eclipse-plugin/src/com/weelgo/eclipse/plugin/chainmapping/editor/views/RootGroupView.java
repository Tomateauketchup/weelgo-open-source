package com.weelgo.eclipse.plugin.chainmapping.editor.views;

import com.weelgo.chainmapping.core.CMTask;
import com.weelgo.eclipse.plugin.selectionViewer.PurePropertiesViewer;

public class RootGroupView extends PurePropertiesViewer<CMTask> {

	public RootGroupView() {
		addProperty(new GroupNameUiProperty());
		addProperty(new GroupPackageNameUiProperty());
	}

}
