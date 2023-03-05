package com.weelgo.eclipse.plugin.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import com.weelgo.core.CoreUtils;
import com.weelgo.eclipse.plugin.selectionViewer.SelectionView;

public abstract class TextUiProperty<D> extends UiProperty<D, String, Text> {

	@Override
	public Text createUi(Composite parent) {
		Text t = new Text(parent, SWT.BORDER | SWT.SINGLE);
		t.addModifyListener(e -> {
			callViewValidateInputs();
		});
		setWidget(t);
		addValidateWithEnter();
		return t;
	}

	@Override
	public String convertIhmDataToString(String data) {
		return data;
	}

	@Override
	public String getDataFromIHM() {
		if (getWidget() != null) {
			String str = CoreUtils.cleanString(getWidget().getText());
			return str;
		}
		return null;
	}

	public void addValidateWithEnter() {
		SelectionView p = getSelectionViewerPart();
		p.addValidateWithEnter(getWidget());
	}

	@Override
	public Control getControl() {
		return getWidget();
	}

}
