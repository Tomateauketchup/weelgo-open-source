package com.weelgo.eclipse.plugin.chainmapping.editor.views;

import java.util.List;

import com.weelgo.chainmapping.core.CMGroup;
import com.weelgo.core.Color;
import com.weelgo.core.CoreUtils;
import com.weelgo.core.INamedObject;
import com.weelgo.eclipse.plugin.job.CMJob;
import com.weelgo.eclipse.plugin.job.CMModifyGroupBackgroundColorJob;
import com.weelgo.eclipse.plugin.job.CMModifyGroupBorderColorJob;
import com.weelgo.eclipse.plugin.ui.ColorPickupUiProperty;

public class GroupBorderColorUiProperty extends ColorPickupUiProperty<CMGroup> {

	public GroupBorderColorUiProperty() {
		setName("Border color");
		setId("BORDER_COLOR");
	}

	@Override
	public void doPopulate(CMGroup data) {

		Color color = null;
		if (data != null) {
			color = data.getBorderColor();
		}
		setColorToIHM(color);
	}

	@Override
	public Color getDefaultColor() {
		return Color.CREATE_DEFAULT_GROUP_BORDER_COLOR();
	}

	@Override
	public String validateInput(Color dataToValidate) {
		return null;
	}

	@Override
	public String getDataFromObjectString(CMGroup object) {
		if (object != null) {
			return CoreUtils.toString(object.getBorderColor());
		}

		return "";
	}

	@Override
	public List<CMJob> applyChanges(String dataFromIHM) {
		CMModifyGroupBorderColorJob j=CMModifyGroupBorderColorJob.CREATE();
		j.setColor(CoreUtils.toColor(dataFromIHM));

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
