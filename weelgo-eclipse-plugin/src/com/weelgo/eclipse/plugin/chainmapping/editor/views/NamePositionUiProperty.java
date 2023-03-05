package com.weelgo.eclipse.plugin.chainmapping.editor.views;

import java.util.List;

import org.eclipse.jface.viewers.StructuredSelection;

import com.weelgo.chainmapping.core.CMNode;
import com.weelgo.core.CoreUtils;
import com.weelgo.eclipse.plugin.job.CMJob;
import com.weelgo.eclipse.plugin.job.CMModifyNodeNamePositionJob;
import com.weelgo.eclipse.plugin.ui.ListBoxUiProperty;

public class NamePositionUiProperty extends ListBoxUiProperty<CMNode> {

	public NamePositionUiProperty() {
		setName("Name position");
		setId("NAME_POSITION");
	}

	@Override
	public List<String> getListElements() {
		List lst = CoreUtils.putObjectIntoList(CMNode.NAME_LEFT);
		CoreUtils.putObjectIntoList(CMNode.NAME_RIGHT, lst);
		CoreUtils.putObjectIntoList(CMNode.NAME_TOP, lst);
		CoreUtils.putObjectIntoList(CMNode.NAME_BOTTOM, lst);
		return lst;
	}

	@Override
	public void doPopulate(CMNode data) {

		String pos = CMNode.NAME_RIGHT;
		if (data != null) {
			pos = data.getNamePosition();
		}
		if (getWidget() != null) {
			getWidget().setSelection(new StructuredSelection(pos));
		}
	}

	@Override
	public String validateInput(String nameposition) {
		if (CMNode.NAME_BOTTOM.equals(nameposition) == false && CMNode.NAME_TOP.equals(nameposition) == false
				&& CMNode.NAME_RIGHT.equals(nameposition) == false && CMNode.NAME_LEFT.equals(nameposition) == false) {
			return "Please enter a valid name position.";
		}
		return null;
	}

	@Override
	public List<CMJob> applyChanges(String dataFromIHM) {

		CMModifyNodeNamePositionJob j = CMModifyNodeNamePositionJob.CREATE();
		CMNode n = new CMNode();
		n.setNamePosition(dataFromIHM);
		n.setUuid(getData().getUuid());
		n.setModuleUniqueIdentifier(getData().getModuleUniqueIdentifier());
		j.setModuleUniqueIdentifier(n.getModuleUniqueIdentifier());
		j.setSelectedObject(CoreUtils.putObjectIntoList(n));
		j.setOrderIndex(getJobOrder());
		return CoreUtils.putObjectIntoList(j);
	}

	@Override
	public String getDataFromObjectString(CMNode n) {
		if (n != null) {
			return n.getNamePosition();
		}
		return null;
	}

}
