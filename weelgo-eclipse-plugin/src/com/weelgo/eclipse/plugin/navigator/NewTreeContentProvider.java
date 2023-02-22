package com.weelgo.eclipse.plugin.navigator;

import java.util.List;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.jface.viewers.ITreeContentProvider;

import com.weelgo.chainmapping.core.navigator.NavNode;
import com.weelgo.chainmapping.core.navigator.NavigatorModel;


@Creatable
public class NewTreeContentProvider implements ITreeContentProvider {

	private NavigatorModel model;

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof NavNode navNode) {
			return model.getChildsArray(navNode);
		}
		return new Object[0];
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof NavNode navNode) {
			return model.getParentNode(navNode);
		}
		return null;
	}

	@Override
	public Object[] getElements(Object inputElement) {

		return getChildren(inputElement);
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof NavNode navNode) {
			List<NavNode> l = model.getChilds(navNode);

			return l != null && l.size() > 0;
		}

		return false;
	}

	public NavigatorModel getModel() {
		return model;
	}

	public void setModel(NavigatorModel model) {
		this.model = model;
	}

	
}
