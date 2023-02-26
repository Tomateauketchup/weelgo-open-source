package com.weelgo.eclipse.plugin.selectionViewer;

import org.eclipse.swt.widgets.Composite;

public interface ISelectionView<T> {
	
	public Composite createContent(Composite parent);
	public void populateView(T object);
	public void applyChanges();
	public void updateStatus(String message);
	public boolean validateInputs();

}
