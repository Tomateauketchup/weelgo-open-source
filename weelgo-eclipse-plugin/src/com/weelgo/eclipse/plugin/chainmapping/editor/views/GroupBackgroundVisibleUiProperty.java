package com.weelgo.eclipse.plugin.chainmapping.editor.views;

import java.util.List;

import com.weelgo.chainmapping.core.CMGroup;
import com.weelgo.core.CoreUtils;
import com.weelgo.core.INamedObject;
import com.weelgo.eclipse.plugin.job.CMJob;
import com.weelgo.eclipse.plugin.job.CMModifyGroupBackgroundVisibleJob;
import com.weelgo.eclipse.plugin.job.CMModifyGroupNameJob;
import com.weelgo.eclipse.plugin.ui.CheckboxUiProperty;

public class GroupBackgroundVisibleUiProperty extends CheckboxUiProperty<CMGroup> {

	public GroupBackgroundVisibleUiProperty() {
		setName("Background visible");
		setId("BACKGROUND_VISIBLE");
	}

	@Override
	public void doPopulate(CMGroup data) {

		boolean isVisible = false;
		if (data != null) {
			isVisible = data.isBackgroundVisible();
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
		CMModifyGroupBackgroundVisibleJob j=CMModifyGroupBackgroundVisibleJob.CREATE();
		j.setVisible(CoreUtils.toBoolean(dataFromIHM));

		CMGroup n = new CMGroup();

		INamedObject o = getData();
		if (o instanceof CMGroup node) {
			n.setModuleUniqueIdentifier(node.getModuleUniqueIdentifier());
			n.setUuid(node.getUuid());
		}

		j.setModuleUniqueIdentifier(n.getModuleUniqueIdentifier());
		j.setSelectedObject(n);
		j.setOrderIndex(getJobOrder());

		return CoreUtils.putObjectIntoList(j);
	}

}
