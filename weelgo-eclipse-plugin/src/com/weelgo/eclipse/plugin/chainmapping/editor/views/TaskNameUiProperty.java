package com.weelgo.eclipse.plugin.chainmapping.editor.views;

import java.util.List;

import com.weelgo.chainmapping.core.CMNode;
import com.weelgo.core.CoreUtils;
import com.weelgo.core.INamedObject;
import com.weelgo.core.ValidatorUtils;
import com.weelgo.eclipse.plugin.job.CMJob;
import com.weelgo.eclipse.plugin.job.CMModifyTaskNameJob;
import com.weelgo.eclipse.plugin.ui.NamedObjectUiProperty;

public class TaskNameUiProperty extends NamedObjectUiProperty {

	@Override
	public String validateInput(String str) {
		if (!ValidatorUtils.isValidFileName(str)) {
			return "Please enter a valid task name.";
		}
		return null;
	}

	@Override
	public List<CMJob> applyChanges(String dataFromIHM) {
		CMModifyTaskNameJob j = CMModifyTaskNameJob.CREATE();
		j.setNewName(dataFromIHM);
		CMNode n = new CMNode();
		n.setName(dataFromIHM);

		INamedObject o = getData();
		if (o instanceof CMNode node) {
			n.setModuleUniqueIdentifier(node.getModuleUniqueIdentifier());
			n.setUuid(node.getUuid());			
		}

		j.setModuleUniqueIdentifier(n.getModuleUniqueIdentifier());
		j.setSelectedObject(n);
		j.setOrderIndex(getJobOrder());
		return CoreUtils.putObjectIntoList(j);
	}

}
