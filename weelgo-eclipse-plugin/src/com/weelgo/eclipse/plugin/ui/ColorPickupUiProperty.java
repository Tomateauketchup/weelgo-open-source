package com.weelgo.eclipse.plugin.ui;

import org.eclipse.draw2d.GridData;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import com.weelgo.core.Color;
import com.weelgo.core.CoreUtils;
import com.weelgo.eclipse.plugin.ColorFactory;
import com.weelgo.eclipse.plugin.Factory;

public abstract class ColorPickupUiProperty<D> extends UiProperty<D, Color, Button> {

	private Color selectedColor = null;
	private org.eclipse.swt.graphics.Color swtColor = null;
	private Label colorLabel;

	@Override
	public Button createUi(Composite parent) {

		Composite comp = new Composite(parent, SWT.NONE);
		GridLayout layout = Factory.createGridLayoutNoMargin(2);
		comp.setLayout(layout);
		GridDataFactory.fillDefaults().grab(false, false).align(GridData.BEGINNING, GridData.BEGINNING).applyTo(comp);

		colorLabel = new Label(comp, SWT.BORDER);
		GridDataFactory.fillDefaults().hint(20, 10).applyTo(colorLabel);

		Button t = new Button(comp, SWT.PUSH);
		t.setText("Pick color");
		GridDataFactory.fillDefaults().applyTo(t);
		t.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				openColorPickup(t);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				openColorPickup(t);
			}
		});
		setWidget(t);
		return t;
	}

	public void openColorPickup(Button t) {
		ColorDialog dlg = new ColorDialog(t.getShell());
		dlg.setRGB(new RGB(selectedColor.getRed(), selectedColor.getGreen(), selectedColor.getBlue()));
		RGB rgb = dlg.open();
		if (rgb != null) {
			Color c = new Color(rgb.red, rgb.green, rgb.blue);
			setColorToIHM(c);
		}
		callViewValidateInputs();
	}

	@Override
	public String convertIhmDataToString(Color data) {
		return CoreUtils.toString(data);
	}

	@Override
	public Color getDataFromIHM() {
		return selectedColor;
	}

	@Override
	public Control getControl() {
		return getWidget();
	}

	@Override
	public void disposeObject() {
		if (swtColor != null) {
			swtColor.dispose();
		}
		super.disposeObject();
	}

	public abstract Color getDefaultColor();

	public void setColorToIHM(Color color) {
		if (swtColor != null) {
			swtColor.dispose();
		}
		if (color == null) {
			color = getDefaultColor();
		}
		selectedColor = color;
		swtColor = ColorFactory.createColor(color);

		if (colorLabel != null) {
			colorLabel.setBackground(swtColor);
		}
	}
}
