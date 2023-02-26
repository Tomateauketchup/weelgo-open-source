package com.weelgo.eclipse.plugin.undoredo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.ui.parts.GraphicalEditor;
import org.eclipse.gef.ui.parts.GraphicalViewerImpl;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorPart;

import com.weelgo.chainmapping.core.CMGroup;
import com.weelgo.chainmapping.core.CMModuleService;
import com.weelgo.chainmapping.core.CMTask;
import com.weelgo.core.CoreUtils;
import com.weelgo.core.IUuidObject;
import com.weelgo.core.undoredo.UndoRedoManager;
import com.weelgo.core.undoredo.UndoRedoNode;
import com.weelgo.eclipse.plugin.CMEvents;
import com.weelgo.eclipse.plugin.CMService;
import com.weelgo.eclipse.plugin.ColorFactory;
import com.weelgo.eclipse.plugin.CurrentSelectionService;
import com.weelgo.eclipse.plugin.Factory;
import com.weelgo.eclipse.plugin.ImagesFactory;
import com.weelgo.eclipse.plugin.SelectionAdapter;
import com.weelgo.eclipse.plugin.job.CMJob;

@Creatable
public class WeelgoUndoRedoViewPart {

	@Inject
	CMService cmServices;

	private Composite container;

	private ComboViewer comboModules;

	private GraphicalViewer grapView;

	@Inject
	ESelectionService selectionService;

	@Inject
	SelectionAdapter selectionAdapter;

	@Inject
	private UndoRedoModel undoRedoModel;

	private Label savedLabel;

	private Composite selectorComposite;

	@PostConstruct
	public void postConstruct(Composite parent, EMenuService menuService, UndoRedoEditDomain domain) {

		parent.setBackground(ColorFactory.WHITE_COLOR);
		container = new Composite(parent, SWT.NONE);

		GridLayout layout =Factory.createGridLayout(1);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		container.setLayout(layout);

		selectorComposite = new Composite(container, SWT.NONE);
//		compTmp.setBackground(ColorFactory.BLUE_COLOR);
		selectorComposite.setLayout(Factory.createGridLayout(3));

		Image image = ImagesFactory.getIconImage(ImagesFactory.CHAIN_MAPPING_ICON);
		Label imgLabel = new Label(selectorComposite, SWT.NONE);
		imgLabel.setImage(image);
		GridDataFactory.fillDefaults().applyTo(imgLabel);

		comboModules = new ComboViewer(selectorComposite, SWT.READ_ONLY);
		comboModules.setContentProvider(new ComboContentProvider());
		comboModules.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element != null && element instanceof CMModuleService) {
					CMModuleService ser = (CMModuleService) element;
					return ser.getName();
				}
				return super.getText(element);
			}
		});
		GridDataFactory.fillDefaults().grab(true, false).applyTo(comboModules.getControl());
		comboModules.addPostSelectionChangedListener(event -> {
			selectionService.setSelection(getSelectedModule());
			refreshView();
		});

		comboModules.setInput(cmServices);

		savedLabel = new Label(selectorComposite, SWT.NONE);
		GridDataFactory.fillDefaults().applyTo(savedLabel);

		grapView = new ScrollingGraphicalViewer();
		grapView.createControl(container);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(grapView.getControl());
		grapView.getControl().setBackground(ColorFactory.WHITE_COLOR);

		undoRedoModel.setServiceRetriever(t -> {
			return getSelectedModule();
		});
		grapView.setEditPartFactory(new UndoRedoEditPartFactory());
		grapView.setContents(undoRedoModel);
		grapView.setEditDomain(domain);
		menuService.registerContextMenu(grapView.getControl(), "com.weelgo.eclipse.plugin.undoredo.ContextMenu");

		Factory.askFirstModulesLoad();

	}

	public CMModuleService getSelectedModule() {

		return selectionAdapter.find(comboModules.getSelection(), CMModuleService.class);
	}

	public void refreshView() {

		CMJob.updateUI("Weelgo Undo/Redo", (IProgressMonitor) -> {

			comboModules.refresh();
			grapView.getContents().refresh();

			CMModuleService s = getSelectedModule();
			boolean isDirty = s != null && s.isDirty();

			savedLabel.setText(isDirty ? "*" : "");
			selectorComposite.pack();
//			grapView.getControl().pack();

		});
	}

	@Focus
	public void focus() {
		selectionService.setSelection(getSelectedModule());
	}

	@Inject
	@Optional
	public void setSelectionCHanged(@UIEventTopic(CMEvents.SELECTION_CHANGED) Object selection) {
		CMModuleService serv = selectionAdapter.findModuleService(selection);
		if (serv != null) {
			boolean changeSelection = true;
			CMModuleService currentSelection = getSelectedModule();
			if (currentSelection != null && CoreUtils.isStrictlyEqualsString(
					currentSelection.getModuleUniqueIdentifier(), serv.getModuleUniqueIdentifier())) {
				changeSelection = false;
			}
			if (changeSelection) {
				comboModules.setSelection(new StructuredSelection(serv));
			}
		}
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
	public void getGroupCreatedEvent(@UIEventTopic(CMEvents.GROUP_CREATED) CMGroup gp) {
		refreshView();
	}

	@Inject
	@Optional
	public void getTaskCreatedEvent(@UIEventTopic(CMEvents.TASK_CREATED) CMTask tsk) {
		refreshView();
	}

	@Inject
	@Optional
	public void getNodesRemovedEvent(@UIEventTopic(CMEvents.NODES_REMOVED) List nodes) {
		refreshView();
	}

	@Inject
	@Optional
	public void getNodesMovedEvent(@UIEventTopic(CMEvents.NODES_POSITION_CHANGED) List nodes) {
		refreshView();
	}

	@Inject
	@Optional
	public void getModuleUndoRedoDoneOperationEvent(
			@UIEventTopic(CMEvents.MODULE_UNDO_REDO_OPERATION_DONE) String modulId) {
		refreshView();
	}

	class ComboContentProvider implements IStructuredContentProvider {

		@Override
		public Object[] getElements(Object inputElement) {
			if (inputElement != null && inputElement instanceof CMService) {
				CMService ser = (CMService) inputElement;
				List<CMModuleService> modules = ser.getModulesManager().getServices();
				if (modules != null && modules.size() > 0) {
					return modules.toArray(new CMModuleService[modules.size()]);
				}
			}
			return new Object[0];
		}
	}
}
