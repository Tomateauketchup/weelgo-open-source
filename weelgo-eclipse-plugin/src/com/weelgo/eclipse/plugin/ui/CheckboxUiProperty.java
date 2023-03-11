package com.weelgo.eclipse.plugin.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.weelgo.core.CoreUtils;

public abstract class CheckboxUiProperty<D> extends UiProperty<D, Boolean, Button> {

	@Override
	public Button createUi(Composite parent) {
		Button t = new Button(parent, SWT.CHECK);
		t.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				callViewValidateInputs();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				callViewValidateInputs();
			}
		});
		setWidget(t);
		return t;
	}

	@Override
	public String convertIhmDataToString(Boolean data) {
		return CoreUtils.toString(data);
	}

	@Override
	public Boolean getDataFromIHM() {
		if (getWidget() != null) {
			return getWidget().getSelection();

		}
		return false;
	}

	@Override
	public Control getControl() {
		return getWidget();
	}

}
