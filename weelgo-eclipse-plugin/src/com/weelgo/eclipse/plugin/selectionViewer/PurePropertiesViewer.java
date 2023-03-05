package com.weelgo.eclipse.plugin.selectionViewer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import com.weelgo.core.CoreUtils;
import com.weelgo.eclipse.plugin.Factory;
import com.weelgo.eclipse.plugin.job.CMJob;
import com.weelgo.eclipse.plugin.ui.UiProperty;

public class PurePropertiesViewer<T> extends SelectionView<T> {

	private List<UiProperty> propertiesList = new ArrayList<>();

	@Override
	public void disposeObject() {
		CoreUtils.disposeList(propertiesList);
		super.disposeObject();
	}

	public void addProperty(UiProperty p) {
		if (p != null && propertiesList != null) {
			propertiesList.add(p);
		}
	}

	@Override
	public Composite createContent(Composite parent) {

		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(Factory.createGridLayout(2));

		if (propertiesList != null) {
			for (UiProperty uiProperty : propertiesList) {
				if (uiProperty != null) {
					uiProperty.setViewRetriever(t -> {
						return this;
					});
					String name = uiProperty.getName();

					Label label = new Label(mainComposite, SWT.NONE);
					label.setText(name + ":");

					uiProperty.createUi(mainComposite);
					Control w = uiProperty.getControl();
					GridDataFactory.fillDefaults().grab(true, false).applyTo(w);
				}
			}
		}
		return mainComposite;
	}

	@Override
	public void populateView(T object) {
		if (propertiesList != null) {
			for (UiProperty uiProperty : propertiesList) {
				if (uiProperty != null) {
					uiProperty.populate(object);
				}
			}
		}

	}

	@Override
	public List<CMJob> applyChanges() {
		List<CMJob> lst = new ArrayList<>();

		if (propertiesList != null) {			
			for (UiProperty uiProperty : propertiesList) {
				if (uiProperty != null && uiProperty.isDataEquals(uiProperty.getData()) == false) {
					CoreUtils.putListIntoList(uiProperty.applyChanges(), lst);
				}
			}
		}
		return lst;
	}

	@Override
	public boolean validateInputs() {
		if (propertiesList != null) {
			for (UiProperty uiProperty : propertiesList) {
				if (uiProperty != null) {
					String msg = uiProperty.validateInput();
					if (isNotNullOrEmpty(msg)) {
						updateStatus(msg);
						return false;
					}
				}
			}
		}
		updateStatus(null);
		return true;
	}

	@Override
	public boolean isDataEquals(T object) {
		if (propertiesList != null) {
			for (UiProperty uiProperty : propertiesList) {
				if (uiProperty != null) {
					boolean isEqual = uiProperty.isDataEquals(object);
					if (!isEqual) {
						return false;
					}
				}
			}
		}
		return true;
	}

	public List<UiProperty> getPropertiesList() {
		return propertiesList;
	}

	public void setPropertiesList(List<UiProperty> propertiesList) {
		this.propertiesList = propertiesList;
	}

}
