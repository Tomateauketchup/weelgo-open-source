package com.weelgo.eclipse.plugin.chainmapping.editor;

import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;

import com.weelgo.chainmapping.core.CMModuleService;
import com.weelgo.chainmapping.core.CMTask;
import com.weelgo.eclipse.plugin.CurrentSelectionService;
import com.weelgo.eclipse.plugin.Factory;
import com.weelgo.eclipse.plugin.chainmapping.editor.actions.CreateTaskAction;
import com.weelgo.eclipse.plugin.chainmapping.editor.actions.RemoveNodesAction;

public class DynamicContextMenu extends ContextMenuProvider {

	private CurrentSelectionService selectionService;
	private ActionRegistry actionRegistry;

	public DynamicContextMenu(EditPartViewer viewer, ActionRegistry actionRegistry) {
		super(viewer);
		selectionService = Factory.getCurrentSelectionService();
		this.actionRegistry = actionRegistry;
	}

	@Override
	public void buildContextMenu(IMenuManager menu) {

		if (selectionService.find(CMTask.class) != null) {
			IAction action = getActionRegistry().getAction(RemoveNodesAction.REMOVE_NODES);
			menu.add(action);
		} else if (selectionService.find(CMModuleService.class) != null) {
			IAction action = getActionRegistry().getAction(CreateTaskAction.CREATE_TASK);
			menu.add(action);
		}

	}

	public ActionRegistry getActionRegistry() {
		return actionRegistry;
	}

	public void setActionRegistry(ActionRegistry actionRegistry) {
		this.actionRegistry = actionRegistry;
	}

	//TODO faire déplacement d'un groupe de noeuds pour pas afficher un truc de déglingo dans le undo redo
	//TODO faire suppression d'un groupe de noeuds
	
}
