package com.weelgo.eclipse.plugin.selectionViewer;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import com.weelgo.chainmapping.core.CMGroup;
import com.weelgo.chainmapping.core.CMNode;
import com.weelgo.chainmapping.core.CMTask;
import com.weelgo.core.CoreUtils;
import com.weelgo.core.IUuidObject;
import com.weelgo.eclipse.plugin.CMEvents;
import com.weelgo.eclipse.plugin.ColorFactory;
import com.weelgo.eclipse.plugin.CurrentSelectionService;
import com.weelgo.eclipse.plugin.Factory;
import com.weelgo.eclipse.plugin.ImagesFactory;
import com.weelgo.eclipse.plugin.chainmapping.editor.views.GroupView;
import com.weelgo.eclipse.plugin.chainmapping.editor.views.RootGroupView;
import com.weelgo.eclipse.plugin.chainmapping.editor.views.TaskView;
import com.weelgo.eclipse.plugin.job.CMJob;
import com.weelgo.eclipse.plugin.job.CMJobHandler;

public class SelectionViewerPart {

	@Inject
	CurrentSelectionService currentSelectionService;

	private Composite mainComposite;
	private Object currentData = null;
	private ISelectionView currentView;
	private boolean pinned = false;
	private boolean dataEquals = true;

	private MUIElement applyChangesButton;

	private MUIElement cancelChangesButton;

	private Composite messageBoxComposite;

	private Label messageLabel;

	private Composite viewComposite;

	@PostConstruct
	public void postConstruct(Composite parent, EModelService service, MPart part) {

		applyChangesButton = service.find("com.weelgo.eclipse.plugin.selectionViewer.ApplyChanges", part.getToolbar());
		applyChangesButton.setVisible(false);

		cancelChangesButton = service.find("com.weelgo.eclipse.plugin.selectionViewer.CancelChanges",
				part.getToolbar());
		cancelChangesButton.setVisible(false);

		parent.setBackground(ColorFactory.WHITE_COLOR);
		mainComposite = new Composite(parent, SWT.NONE);
		GridLayout layout = Factory.createGridLayout(1);
		mainComposite.setLayout(layout);

		messageBoxComposite = new Composite(mainComposite, SWT.BORDER);
		messageBoxComposite.setBackground(ColorFactory.LIGHT_RED_COLOR);
		layout = Factory.createGridLayout(1);
		messageBoxComposite.setLayout(layout);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(messageBoxComposite);

		showMessageBox(false);

		messageLabel = new Label(messageBoxComposite, SWT.WRAP);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(messageLabel);

		viewComposite = new Composite(mainComposite, SWT.NONE);
		layout = Factory.createGridLayoutNoMargin(1);
		viewComposite.setLayout(layout);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(viewComposite);

	}

	public void showMessageBox(boolean show) {
		messageBoxComposite.setVisible(show);
		GridData gd = (GridData) messageBoxComposite.getLayoutData();
		gd.exclude = !show;
		mainComposite.layout();
	}

	@Inject
	@Optional
	public void setSelectionCHanged(@UIEventTopic(CMEvents.SELECTION_CHANGED) Object selection) {

		boolean locked = pinned || !dataEquals;

		if (!locked) {
			doSelectionChanged(selection);
		}

	}

	public void doSelectionChanged(Object selection) {
		// En fonction de la sélection on affiche des données spécifiques
		ISelectionView view = null;
		Object selectedObject = null;

		List multiSelection = currentSelectionService.findListMulti(CMGroup.class, CMNode.class);
		if (multiSelection != null) {
			if (multiSelection.size() > 1) {
				// Multi sélection

				view = new MultiSelectionViewer();
				view.setPart(this);
				selectedObject = multiSelection;
			} else if (multiSelection.size() > 0) {
				Object task = multiSelection.get(0);
				if (task != null) {
					view = createViewFromObject(task);
					if (view != null) {
						view.setPart(this);
						selectedObject = task;
					}
				}
			}
		}

		if (!isSameSelection(selectedObject)) {
			currentData = selectedObject;
			if (currentView != null) {
				currentView.disposeObject();
			}
			currentView = view;
			dataEquals = true;
			cleanComposite(viewComposite);
			if (currentView != null) {
				Composite comp = currentView.createContent(viewComposite);
				GridDataFactory.fillDefaults().grab(true, false).applyTo(comp);
			}

		}

		if (currentView != null) {
			currentView.populateView(currentData);
			currentView.validateInputs();
		}

		messageBoxComposite.layout();
		viewComposite.layout();
		mainComposite.layout();
	}

	public static PurePropertiesViewer createViewFromObject(Object o) {
		if (o != null) {
			if (o instanceof CMTask) {
				return new TaskView();
			} else if (o instanceof CMGroup gp) {
				if (gp.isModule()) {
					return new RootGroupView();
				} else {
					return new GroupView();
				}

			}
		}
		return null;
	}

	public boolean isSameSelection(Object newSelection) {
		if (currentData == null && newSelection == null) {
			return true;
		}
		if (currentData != null) {
			return currentData.equals(newSelection);
		}
		return false;
	}

	public void updateStatus(String message) {
		boolean hasPb = CoreUtils.isNotNullOrEmpty(message);
		if (currentView != null) {
			dataEquals = currentView.isDataEquals(currentData);
			if (dataEquals) {
				applyChangesButton.setVisible(false);
				cancelChangesButton.setVisible(false);
			} else {
				applyChangesButton.setVisible(!hasPb);
				cancelChangesButton.setVisible(true);
			}
		}
		if (hasPb) {
			messageLabel.setText(message);
			showMessageBox(true);
		} else {
			showMessageBox(false);
		}
	}

	public void cancelChanged() {
		if (currentView != null) {
			currentView.populateView(currentData);
			updateStatus(null);
		}
	}

	public void applyChanges() {
		if (currentView != null) {
			if (currentView.validateInputs()) {
				List jobs = currentView.applyChanges();
				doChanges(jobs);
			}
		}
	}

	public void doChanges(List<CMJob> jobs) {

		CMJob.runMultipleJobs(jobs, new CMJobHandler() {

			@Override
			public void jobEnded(String status) {

				CMJob.updateUI(t -> {
					if (CMJobHandler.STATUS_OK.equals(status)) {
						refresh();
					} else {
						updateStatus("Error occured. Please retry.");
					}
				});

			}
		}, true, "Modify element", ImagesFactory.MODIFY_ICON);
	}

	public void refresh() {

		dataEquals = true;
		currentData = Factory.findThisObject(currentData);
		doSelectionChanged(currentData);
	}

	public void applyChangedDone() {
		refresh();
	}

	// TODO faire verrouiller vue de sélection quand on a commencé à faire un
	// changement pour pas perdre les données

	public static void cleanComposite(Composite c) {
		if (c != null) {
			Control[] childs = c.getChildren();
			if (childs != null) {
				for (Control ct : childs) {
					if (ct != null && !ct.isDisposed()) {
						ct.dispose();
					}
				}
			}
			c.layout(true, true);

		}
	}

	@Focus
	public void focus() {

	}

	public void pin(boolean pin) {
		pinned = pin;
	}

	public Object getCurrentData() {
		return currentData;
	}

}
