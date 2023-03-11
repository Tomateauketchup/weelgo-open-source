package com.weelgo.eclipse.plugin.chainmapping.editor.views;

import java.util.List;

import com.weelgo.chainmapping.core.CMGroup;
import com.weelgo.core.CoreUtils;
import com.weelgo.eclipse.plugin.job.CMJob;
import com.weelgo.eclipse.plugin.ui.CheckboxUiProperty;

public class GroupBorderVisibleUiProperty extends CheckboxUiProperty<CMGroup> {

	public GroupBorderVisibleUiProperty() {
		setName("Border visible");
		setId("BORDER_VISIBLE");
	}

	@Override
	public void doPopulate(CMGroup data) {

		boolean isVisible = false;
		if (data != null) {
			isVisible = data.isBorderVisible();
		}
		if (getWidget() != null) {
			getWidget().setSelection(isVisible);
		}
	}

	@Override
	public String validateInput(Boolean dataToValidate) {
		return null;
	}

	@Override
	public String getDataFromObjectString(CMGroup object) {
		if (object != null) {
			return CoreUtils.toString(object.isBackgroundVisible());
		}

		return CoreUtils.toString(false);
	}

	@Override
	public List<CMJob> applyChanges(String dataFromIHM) {

		return null;
	}

}
