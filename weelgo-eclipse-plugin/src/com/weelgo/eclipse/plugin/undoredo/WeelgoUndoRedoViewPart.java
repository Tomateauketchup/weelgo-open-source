package com.weelgo.eclipse.plugin.undoredo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbench;

import com.weelgo.chainmapping.core.CMGroup;
import com.weelgo.chainmapping.core.CMModuleService;
import com.weelgo.core.undoredo.UndoRedoManager;
import com.weelgo.core.undoredo.UndoRedoNode;
import com.weelgo.eclipse.plugin.CMEvents;
import com.weelgo.eclipse.plugin.CMService;
import com.weelgo.eclipse.plugin.ImagesFactory;
import com.weelgo.eclipse.plugin.job.CMJob;

@Creatable
public class WeelgoUndoRedoViewPart {

	@Inject
	IWorkbench workbench;

	@Inject
	CMService cmServices;

	private TableViewer viewer;

	private Composite container;

	private ComboViewer comboModules;

	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		@Override
		public String getColumnText(Object obj, int index) {
			String label = "Operation";
			if (obj != null && obj instanceof Node) {
				Node nd = (Node) obj;

				UndoRedoNode n = nd.n;
				Object data = n.getInfoData();
				if (data instanceof UndoRedoInfoData) {
					UndoRedoInfoData info = (UndoRedoInfoData) data;
					label = info.getLabel();
				}
				if (nd.isCurrent) {
					label = "-> " + label;
				}
			}
			return label;
		}

		@Override
		public Image getColumnImage(Object obj, int index) {

			String image = ImagesFactory.MODIFY_ICON;
			if (obj != null && obj instanceof Node) {
				Node nd = (Node) obj;

				UndoRedoNode n = nd.n;
				Object data = n.getInfoData();
				if (data instanceof UndoRedoInfoData) {
					UndoRedoInfoData info = (UndoRedoInfoData) data;
					image = info.getIcon();
				}
			}

			return ImagesFactory.getIconImage(image);
		}

		@Override
		public Image getImage(Object obj) {
			return workbench.getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
	}

	class Node {
		private UndoRedoNode n;
		private boolean isCurrent = false;
	}

	class TableViewerContentProvider implements IStructuredContentProvider {

		@Override
		public Object[] getElements(Object inputElement) {
			List<Node> arl = new ArrayList();
			if (inputElement != null && inputElement instanceof CMService) {
				CMService ser = (CMService) inputElement;
				ISelection select = comboModules.getSelection();
				if (select != null && select instanceof IStructuredSelection) {
					IStructuredSelection structSelec = (IStructuredSelection) select;
					if (structSelec != null) {
						Object o = structSelec.getFirstElement();
						if (o != null && o instanceof CMModuleService) {
							CMModuleService module = (CMModuleService) o;
							UndoRedoManager undoRedoManager = module.getUndoRedoManager();
							if (undoRedoManager != null) {
								UndoRedoNode firstElem = undoRedoManager.getFirstNode();
								if (firstElem != null) {
									UndoRedoNode elemTmp = firstElem;

									do {

										Node n = new Node();
										n.n = elemTmp;
										n.isCurrent = elemTmp.equals(undoRedoManager.getCurrentNode());
										arl.add(n);

										elemTmp = elemTmp.getLastChildNode();

									} while (elemTmp != null);
								}

							}
						}
					}
				}
			}
			Collections.reverse(arl);
			return arl.toArray(new Node[arl.size()]);
		}
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

	public void postConstruct(Composite parent) {

		parent.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));

		container = new Composite(parent, SWT.NONE);

		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;

		Image image = ImagesFactory.getIconImage(ImagesFactory.CHAIN_MAPPING_ICON);
		Label imgLabel = new Label(container, SWT.NONE);
		imgLabel.setImage(image);
		GridDataFactory.fillDefaults().applyTo(imgLabel);

		comboModules = new ComboViewer(container, SWT.READ_ONLY);
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
//		GridDataFactory.fillDefaults().applyTo(comboModules.getControl());
		comboModules.addPostSelectionChangedListener(event -> {
			refreshView();
		});

		comboModules.setInput(cmServices);

		viewer = new TableViewer(container, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		GridDataFactory.fillDefaults().span(2, 1).grab(true, true).applyTo(viewer.getControl());
		viewer.setContentProvider(new TableViewerContentProvider());

		viewer.setInput(cmServices);
		viewer.setLabelProvider(new ViewLabelProvider());
	}

	public void moduleSelected(String moduleUniqueIdentifier) {

	}

	public void refreshView() {

		CMJob.updateUI("Weelgo Navigator", (IProgressMonitor) -> {

			comboModules.refresh();
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
	public void getGroupCreatedEvent(@UIEventTopic(CMEvents.GROUP_CREATED) CMGroup gp) {
		refreshView();
	}

	@Inject
	@Optional
	public void getModuleUndoRedoDoneOperationEvent(
			@UIEventTopic(CMEvents.MODULE_UNDO_REDO_OPERATION_DONE) String modulId) {
		refreshView();
	}
}
