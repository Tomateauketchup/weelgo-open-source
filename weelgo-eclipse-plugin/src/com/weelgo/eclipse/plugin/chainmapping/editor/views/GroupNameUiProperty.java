package com.weelgo.eclipse.plugin.chainmapping.editor.views;

import java.util.List;

import com.weelgo.chainmapping.core.CMGroup;
import com.weelgo.core.CoreUtils;
import com.weelgo.core.INamedObject;
import com.weelgo.core.ValidatorUtils;
import com.weelgo.eclipse.plugin.job.CMJob;
import com.weelgo.eclipse.plugin.job.CMModifyGroupNameJob;
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

		CMModifyGroupNameJob j=CMModifyGroupNameJob.CREATE();
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
