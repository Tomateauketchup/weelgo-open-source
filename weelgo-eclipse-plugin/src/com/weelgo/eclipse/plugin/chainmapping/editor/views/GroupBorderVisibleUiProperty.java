package com.weelgo.eclipse.plugin.chainmapping.editor.views;

import java.util.List;

import com.weelgo.chainmapping.core.CMGroup;
import com.weelgo.core.CoreUtils;
import com.weelgo.core.INamedObject;
import com.weelgo.eclipse.plugin.job.CMJob;
import com.weelgo.eclipse.plugin.job.CMModifyGroupBackgroundVisibleJob;
import com.weelgo.eclipse.plugin.job.CMModifyGroupBorderVisibleJob;
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
			return CoreUtils.toString(object.isBorderVisible());
		}

		return CoreUtils.toString(false);
	}

	@Override
	public List<CMJob> applyChanges(String dataFromIHM) {
		CMModifyGroupBorderVisibleJob j=CMModifyGroupBorderVisibleJob.CREATE();
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
