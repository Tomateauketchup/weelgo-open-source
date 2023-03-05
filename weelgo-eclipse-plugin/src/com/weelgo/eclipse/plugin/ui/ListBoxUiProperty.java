package com.weelgo.eclipse.plugin.ui;

import java.util.List;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.weelgo.core.CoreUtils;
import com.weelgo.eclipse.plugin.Factory;

public abstract class ListBoxUiProperty<D> extends UiProperty<D, String, ComboViewer> {

	@Override
	public ComboViewer createUi(Composite parent) {

		ComboViewer comboModules = new ComboViewer(parent, SWT.READ_ONLY);
		setWidget(comboModules);
		comboModules.addPostSelectionChangedListener(event -> {
			callViewValidateInputs();
		});
		comboModules.setContentProvider(new IStructuredContentProvider() {

			@Override
			public Object[] getElements(Object inputElement) {

				return CoreUtils.transformListToStringArray(getListElements());
			}
		});
		comboModules.setLabelProvider(new LabelProvider());
		comboModules.setInput(getListElements());

		return comboModules;
	}

	public abstract List<String> getListElements();

	@Override
	public Control getControl() {
		return getWidget().getControl();
	}

	@Override
	public String getDataFromIHM() {

		if (getWidget() != null) {
			String s = Factory.getSelectionAdapter().find(getWidget().getSelection(), String.class);
			s = cleanString(s);
			return s;
		}
		return null;
	}

	@Override
	public String convertIhmDataToString(String data) {
		return data;
	}

}
