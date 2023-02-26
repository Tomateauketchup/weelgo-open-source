package com.weelgo.eclipse.plugin.chainmapping.editor.actions;

import java.util.List;

import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IWorkbenchPart;

import com.weelgo.eclipse.plugin.Factory;
import com.weelgo.eclipse.plugin.SelectionAdapter;
import com.weelgo.eclipse.plugin.chainmapping.editor.ChainMappingEditor;

public abstract class GenericSelectionAction extends SelectionAction {

	public GenericSelectionAction(IWorkbenchPart part) {
		super(part);
		setLazyEnablementCalculation(false);
	}

	public SelectionAdapter getSelectionAdapter() {
		return Factory.getSelectionAdapter();
	}

	public <T> T find(Class<T> wantedClass) {
		return getSelectionAdapter().find(getSelection(), wantedClass);
	}

	public <T> List<T> findList(Class<T> wantedClass) {
		return getSelectionAdapter().findList(getSelection(), wantedClass);
	}

	public Point getCursorPosition() {
		return Factory.getCursorPosition(getChainMappingEditor().getGraphicalViewer().getControl());
	}

	public ChainMappingEditor getChainMappingEditor() {
		return (ChainMappingEditor) getWorkbenchPart();
	}

	@Override
	protected boolean calculateEnabled() {
		return true;
	}
}
