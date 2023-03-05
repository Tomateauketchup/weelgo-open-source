package com.weelgo.eclipse.plugin.selectionViewer;

import java.util.List;

import org.eclipse.swt.widgets.Composite;

import com.weelgo.core.IDisposableObject;
import com.weelgo.eclipse.plugin.job.CMJob;

public interface ISelectionView<T> extends IDisposableObject{

	public SelectionViewerPart getPart();

	public void setPart(SelectionViewerPart part);

	public Composite createContent(Composite parent);

	public void populateView(T object);

	public List<CMJob> applyChanges();

	public void updateStatus(String message);

	public boolean validateInputs();

	public boolean isDataEquals(T object);

}
