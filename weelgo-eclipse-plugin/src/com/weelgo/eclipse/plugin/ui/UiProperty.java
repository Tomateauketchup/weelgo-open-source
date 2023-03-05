package com.weelgo.eclipse.plugin.ui;

import java.util.List;
import java.util.function.Function;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.weelgo.core.CoreUtils;
import com.weelgo.core.IDisposableObject;
import com.weelgo.core.INamedObject;
import com.weelgo.eclipse.plugin.job.CMJob;
import com.weelgo.eclipse.plugin.selectionViewer.SelectionView;

public abstract class UiProperty<D, E, W> implements IDisposableObject {

	private int jobOrder = 0;
	private Function<Void, SelectionView> viewRetriever;
	private String name;
	private String id;
	private W widget;
	private D data;

	public abstract W createUi(Composite parent);

	public abstract void doPopulate(D data);

	public abstract Control getControl();

	public void populate(D data) {
		setData(data);
		doPopulate(data);
	}

	public void callViewValidateInputs() {
		SelectionView p = getSelectionViewerPart();
		p.validateInputs();
	}

	@Override
	public void disposeObject() {

	}

	public String validateInput() {
		return validateInput(getDataFromIHM());
	}

	public abstract String validateInput(E dataToValidate);

	/**
	 * Doit retourner null si l'ihm n'est pas initialisée
	 * 
	 * @return
	 */
	public abstract E getDataFromIHM();

	public boolean isDataEquals(D object, String dataFromIhm) {
		String str = "";
		if (object != null) {
			str = getDataFromObjectString(object);
		}
		str = cleanString(str);
		return isStrictlyEqualsString(str, dataFromIhm);
	}

	public boolean isDataEquals(D object) {
		return isDataEquals(object, getDataFromIHMString());
	}

	public List<CMJob> applyChanges() {
		return applyChanges(getDataFromIHMString());
	}

	public abstract List<CMJob> applyChanges(String dataFromIHM);

	public String getDataFromObjectString() {
		return getDataFromObjectString(getData());
	}

	public abstract String getDataFromObjectString(D object);

	public abstract String convertIhmDataToString(E data);

	public String getDataFromIHMString() {
		E ob = getDataFromIHM();
		if (ob != null) {
			return convertIhmDataToString(ob);
		}
		return getDataFromObjectString();
	}

	public D getData() {
		return data;
	}

	public void setData(D data) {
		this.data = data;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public W getWidget() {
		return widget;
	}

	public void setWidget(W widget) {
		this.widget = widget;
	}

	public String cleanString(String str) {
		return CoreUtils.cleanString(str);
	}

	public boolean isStrictlyEqualsString(String o1, String o2) {

		return CoreUtils.isStrictlyEqualsString(o1, o2);

	}

	public Function<Void, SelectionView> getViewRetriever() {
		return viewRetriever;
	}

	public void setViewRetriever(Function<Void, SelectionView> viewRetriever) {
		this.viewRetriever = viewRetriever;
	}

	public SelectionView getSelectionViewerPart() {
		if (viewRetriever != null) {
			return viewRetriever.apply(null);
		}
		return null;
	}

	public int getJobOrder() {
		return jobOrder;
	}

	public void setJobOrder(int jobOrder) {
		this.jobOrder = jobOrder;
	}

}
