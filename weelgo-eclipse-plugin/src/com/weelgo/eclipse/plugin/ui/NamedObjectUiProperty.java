package com.weelgo.eclipse.plugin.ui;

import com.weelgo.core.INamedObject;

public abstract class NamedObjectUiProperty extends TextUiProperty<INamedObject> {

	public NamedObjectUiProperty() {
		setName("Name");
		setId("NAME");
		setJobOrder(1);
	}

	@Override
	public void doPopulate(INamedObject data) {
		String str = "";
		if (data != null) {
			str = data.getName();
		}
		str = cleanString(str);
		if (getWidget() != null) {
			getWidget().setText(str);
		}
	}

	@Override
	public String getDataFromObjectString(INamedObject o) {
		if (o != null) {
			return o.getName();
		}
		return null;
	}

}
