package com.weelgo.eclipse.plugin.selectionViewer;

import com.weelgo.core.CoreUtils;

public abstract class SelectionView<T> implements ISelectionView<T>{
	
	private SelectionViewerPart part;

	public SelectionViewerPart getPart() {
		return part;
	}

	public void setPart(SelectionViewerPart part) {
		this.part = part;
	}
	
	public void updateStatus(String message) {
		if(part!=null)
		{
			part.updateStatus(message);
		}
	}
	public String cleanString(String str)
	{
		return CoreUtils.cleanString(str);
	}

}
