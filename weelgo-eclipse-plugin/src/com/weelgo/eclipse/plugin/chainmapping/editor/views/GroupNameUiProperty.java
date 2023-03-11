package com.weelgo.eclipse.plugin.chainmapping.editor.views;

import java.util.List;

import com.weelgo.core.ValidatorUtils;
import com.weelgo.eclipse.plugin.job.CMJob;
import com.weelgo.eclipse.plugin.ui.NamedObjectUiProperty;

public class GroupNameUiProperty extends NamedObjectUiProperty {

	@Override
	public String validateInput(String str) {
		if (!ValidatorUtils.isValidGroupName(str)) {
			return "Please enter a valid group name.";
		}
		return null;
	}

	@Override
	public List<CMJob> applyChanges(String dataFromIHM) {
		return null;
	}

}
