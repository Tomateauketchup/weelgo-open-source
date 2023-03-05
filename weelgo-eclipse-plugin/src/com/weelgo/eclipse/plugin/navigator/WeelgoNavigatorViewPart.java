package com.weelgo.eclipse.plugin.navigator;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.weelgo.chainmapping.core.CMGroup;
import com.weelgo.chainmapping.core.CMTask;
import com.weelgo.chainmapping.core.navigator.NavigatorModel;
import com.weelgo.core.CoreUtils;
import com.weelgo.eclipse.plugin.CMEvents;
import com.weelgo.eclipse.plugin.CMService;
import com.weelgo.eclipse.plugin.Factory;
import com.weelgo.eclipse.plugin.SelectionAdapter;
import com.weelgo.eclipse.plugin.chainmapping.editor.ChainMappingEditor;
import com.weelgo.eclipse.plugin.job.CMJob;

public class WeelgoNavigatorViewPart {

	private TreeViewer viewer;

	private NavigatorModel model;

	@Inject
	private CMService services;

	@Inject
	SelectionAdapter selectionAdapter;

	@PostConstruct
	public void postConstruct(Composite parent, EMenuService menuService, IEclipseContext eclipseContext,
			ESelectionService selectionService, NewTreeContentProvider contentProvier,
			NewTreeLabelProvider labelProvider) {

		model = new NavigatorModel();
		model.update(services.getModulesManager());
		contentProvier.setModel(model);

		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);

		viewer.setContentProvider(contentProvier);
		viewer.setLabelProvider(labelProvider);
		viewer.setInput(model);
		viewer.addSelectionChangedListener(event -> {
			IStructuredSelection selection = viewer.getStructuredSelection();
			selectionService.setSelection(selection.getFirstElement());
		});
		viewer.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {
				String serviceUuid = selectionAdapter
						.findModuleUniqueIdentifierObjectId(viewer.getStructuredSelection());
				if (CoreUtils.isNotNullOrEmpty(serviceUuid)) {
					ChainMappingEditor.openEditor(serviceUuid);
				}
			}
		});
		menuService.registerContextMenu(viewer.getControl(), "com.weelgo.eclipse.plugin.navigator.ContextMenu");

		// TODO faire rafraichierune partie de l'arbre
		// TODO tester créer un module avec plsuieurs sous groupes sauvés. Puis on créé
		// un module dans un de ses groupes. Ensuite on supprime les groupes et on
		// sauvegarder. Les fichiers de groupes doivent avoir disparu
		Factory.askFirstModulesLoad();
	}

	public void refreshView() {

		model.update(services.getModulesManager());

		CMJob.updateUI("Weelgo Navigator", (IProgressMonitor) -> {

			viewer.refresh();

		});
	}

	@Inject
	@Optional
	public void getWorkspaceModifiedEvent(@UIEventTopic(CMEvents.WORKSPACE_FOLDER_MODIFIED) Object o) {
		refreshView();
	}

	@Inject
	@Optional
	public void getAllModulesLoadedEvent(@UIEventTopic(CMEvents.ALL_MODULE_LOADED) Object o) {
		refreshView();
	}

	@Inject
	@Optional
	public void getAllModulesSavedEvent(@UIEventTopic(CMEvents.ALL_MODULE_SAVED) Object o) {
		refreshView();
	}

	@Inject
	@Optional
	public void getModuleCreatedEvent(@UIEventTopic(CMEvents.MODULE_CREATED) String modulId) {
		refreshView();
	}

	@Inject
	@Optional
	public void getModuleLoadedEvent(@UIEventTopic(CMEvents.MODULE_LOADED) String modulId) {
		refreshView();
	}

	@Inject
	@Optional
	public void getModuleSavedEvent(@UIEventTopic(CMEvents.MODULE_SAVED) String modulId) {
		refreshView();
	}

	@Inject
	@Optional
	public void getNodesRemovedEvent(@UIEventTopic(CMEvents.NODES_REMOVED) List nodes) {
		refreshView();
	}

	@Inject
	@Optional
	public void getNodesPositionChangedEvent(@UIEventTopic(CMEvents.NODES_POSITION_CHANGED) Object nodes) {
		refreshView();
	}
	
	@Inject
	@Optional
	public void getNodesNamePositionChangedEvent(@UIEventTopic(CMEvents.NODES_NAME_POSITION_CHANGED) Object nodes) {
		refreshView();
	}

	@Inject
	@Optional
	public void getTaskCreatedEvent(@UIEventTopic(CMEvents.TASK_CREATED) CMTask tsk) {
		refreshView();
	}

	@Inject
	@Optional
	public void getTaskNameModifiedEvent(@UIEventTopic(CMEvents.TASK_NAME_MODIFIED) CMTask tsk) {
		refreshView();
	}

	@Inject
	@Optional
	public void getModuleUndoRedoDoneOperationEvent(
			@UIEventTopic(CMEvents.MODULE_UNDO_REDO_OPERATION_DONE) String modulId) {
		refreshView();
	}

	@Inject
	@Optional
	public void getGroupCreatedEvent(@UIEventTopic(CMEvents.GROUP_CREATED) CMGroup gp) {
		refreshView();

		// TODO Bug quand on créé un module dans un module ça ajoute un undo dans
		// l'arbre du module parent
		// TODO mettre le nom de l'objet concerné dans la liste des jobs
		// TOTO mettre des flags sur des undiredoNode qui ne peuvent pas être undo comme
		// la création d'un module par exmple
		// TODO faire modification nom des groupes et du module
		// TODO Le load all module doit vérifier qu'il n'y a pas des modules non
		// sauvés.Si c'est le cas un message doit s'afficher
	}

}
