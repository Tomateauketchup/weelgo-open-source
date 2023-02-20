package com.weelgo.eclipse.plugin.navigator;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.weelgo.chainmapping.core.CMGroup;
import com.weelgo.eclipse.plugin.CMEvents;
import com.weelgo.eclipse.plugin.CMService;
import com.weelgo.eclipse.plugin.Factory;
import com.weelgo.eclipse.plugin.job.CMJob;
import com.weelgo.eclipse.plugin.job.CMLoadAllModulesJob;

public class WeelgoNavigatorViewPart {

	private TreeViewer viewer;

	@PostConstruct
	public void postConstruct(Composite parent, CMService services, EMenuService menuService,
			IEclipseContext eclipseContext, ESelectionService selectionService) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);

		TreeContentProvider cp = Factory.create(TreeContentProvider.class, eclipseContext);
		viewer.setContentProvider(cp);

		TreeLabelProvider pvd = Factory.create(TreeLabelProvider.class, eclipseContext);
		viewer.setLabelProvider(pvd);
		viewer.setInput(services.getModulesManager());
		viewer.addSelectionChangedListener(event -> {
			IStructuredSelection selection = viewer.getStructuredSelection();
			selectionService.setSelection(selection.getFirstElement());
		});
		menuService.registerContextMenu(viewer.getControl(), "com.weelgo.eclipse.plugin.navigator.ContextMenu");

		// TODO faire rafraichierune partie de l'arbre
		Factory.askFirstModulesLoad();
	}

	public void refreshView() {

		CMJob.updateUI("Weelgo Navigator", (IProgressMonitor) -> {

			viewer.refresh();

		});
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
	public void getModuleUndoRedoDoneOperationEvent(
			@UIEventTopic(CMEvents.MODULE_UNDO_REDO_OPERATION_DONE) String modulId) {
		refreshView();
	}

	@Inject
	@Optional
	public void getGroupCreatedEvent(@UIEventTopic(CMEvents.GROUP_CREATED) CMGroup gp) {
		refreshView();

		// TODO Bug quand on fait un save d'un module ça créé un oad sur tous
		// TODO Bug quand on créé un module dans un module ça ajoute un undo dans l'arbre du module parent
		// TODO afficher la liste des jobs réalisés pour undo/redo dans une vue
		// TODO dans la liste des jobs, mettre l'heure de la réalisation
		// TODO mettre le nom de l'objet concerné dans la liste des jobs
		// TODO quand on sélectionn un module, mettre à jour l'affichage de la liste des
		// jobs
		// TOTO mettre des flags sur des undiredoNode qui ne peuvent pas être undo comme
		// la création d'un module par exmple
		// TODO Faire save menu sur chaque module
		// TODO faire modification nom des groupes et du module
		// TODO Le load all module doit vérifier qu'il n'y a pas des modules non
		// sauvés.Si c'est le cas un message doit s'afficher
	}

}
