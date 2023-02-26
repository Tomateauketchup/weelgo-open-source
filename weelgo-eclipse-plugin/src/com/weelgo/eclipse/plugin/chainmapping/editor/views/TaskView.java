package com.weelgo.eclipse.plugin.chainmapping.editor.views;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.weelgo.chainmapping.core.CMTask;
import com.weelgo.core.CoreUtils;
import com.weelgo.core.ValidatorUtils;
import com.weelgo.eclipse.plugin.selectionViewer.SelectionView;

public class TaskView extends SelectionView<CMTask> {

	private Text nameText;

	@Override
	public Composite createContent(Composite parent) {

		Composite mainComposite = new Composite(parent, SWT.NONE);

		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(mainComposite);

		Label label = new Label(mainComposite, SWT.NONE);
		label.setText("Name:");

		nameText = new Text(mainComposite, SWT.BORDER | SWT.SINGLE);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(nameText);
		nameText.addModifyListener(e -> validateInputs());

		validateInputs();

		return mainComposite;

	}

	@Override
	public void populateView(CMTask object) {

		if (object != null) {
			String name = CoreUtils.cleanString(object.getName());
			nameText.setText(name);
		}

	}

	public boolean validateInputs() {

		String name = cleanString(nameText.getText());

		if (!ValidatorUtils.isValidTaskName(name)) {
			updateStatus("Name must be valid");
			return false;
		}

		updateStatus(null);
		return true;
	}

	@Override
	public void applyChanges() {
		String name = cleanString(nameText.getText());
	}

}
