package com.weelgo.eclipse.plugin.chainmapping.editor.views;

import java.util.List;

import com.weelgo.chainmapping.core.CMGroup;
import com.weelgo.core.CoreUtils;
import com.weelgo.core.INamedObject;
import com.weelgo.core.ValidatorUtils;
import com.weelgo.eclipse.plugin.job.CMJob;
import com.weelgo.eclipse.plugin.job.CMModifyGroupNameJob;
import com.weelgo.eclipse.plugin.job.CMModifyGroupPackageNameJob;
import com.weelgo.eclipse.plugin.ui.TextUiProperty;

public class GroupPackageNameUiProperty extends TextUiProperty<CMGroup> {

	public GroupPackageNameUiProperty() {
		setName("Package name");
		setId("PACKAGE_NAME");	
	}

	@Override
	public void doPopulate(CMGroup data) {
		String str = "";
		if (data != null) {
			str = data.getPackageName();
		}
		str = cleanString(str);
		if (getWidget() != null) {
			getWidget().setText(str);
		}
	}

	@Override
	public String getDataFromObjectString(CMGroup o) {
		if (o != null) {
			return o.getPackageName();
		}
		return null;
	}

	@Override
	public String validateInput(String str) {
		if (!ValidatorUtils.isValidPackageName(str)) {
			return "Please enter a valid package name.";
		}
		return null;
	}

	@Override
	public List<CMJob> applyChanges(String dataFromIHM) {
		CMModifyGroupPackageNameJob j=CMModifyGroupPackageNameJob.CREATE();
		j.setNewName(dataFromIHM);

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
