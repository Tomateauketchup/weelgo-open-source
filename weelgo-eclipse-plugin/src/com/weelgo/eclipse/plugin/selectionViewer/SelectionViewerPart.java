package com.weelgo.eclipse.plugin.selectionViewer;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.weelgo.chainmapping.core.CMTask;
import com.weelgo.eclipse.plugin.CMEvents;
import com.weelgo.eclipse.plugin.ColorFactory;
import com.weelgo.eclipse.plugin.CurrentSelectionService;
import com.weelgo.eclipse.plugin.Factory;
import com.weelgo.eclipse.plugin.chainmapping.editor.views.TaskView;

public class SelectionViewerPart {

	@Inject
	CurrentSelectionService currentSelectionService;

	private Composite mainComposite;
	private Object currentSelection = null;
	private ISelectionView currentView;
	private boolean pinned = false;

	@PostConstruct
	public void postConstruct(Composite parent, EModelService service, MPart part) {
		parent.setBackground(ColorFactory.WHITE_COLOR);
		mainComposite = new Composite(parent, SWT.NONE);
		GridLayout layout = Factory.createGridLayout(1);
		mainComposite.setLayout(layout);

	}

	@Inject
	@Optional
	public void setSelectionCHanged(@UIEventTopic(CMEvents.SELECTION_CHANGED) Object selection) {

		if (!pinned) {
			// En fonction de la sélection on affiche des données spécifiques
			ISelectionView view = null;
			Object selectedObject = null;
			CMTask task = currentSelectionService.find(CMTask.class);
			if (task != null) {
				view = new TaskView();
				selectedObject = task;
			}

			if (!isSameSelection(selectedObject)) {
				currentSelection = selectedObject;
				currentView = view;

				cleanComposite(mainComposite);
				if (currentView != null) {
					Composite comp = currentView.createContent(mainComposite);
					GridDataFactory.fillDefaults().grab(true, false).applyTo(comp);
				}

			}
		}
		if (currentView != null) {
			currentView.populateView(currentSelection);
		}

		mainComposite.layout();

	}

	public boolean isSameSelection(Object newSelection) {
		if (currentSelection == null && newSelection == null) {
			return true;
		}
		if (currentSelection != null) {
			return currentSelection.equals(newSelection);
		}
		return false;
	}

	public void applyChanges() {
		if (currentView != null) {
			if (currentView.validateInputs()) {
				currentView.applyChanges();
			}
		}
	}
	
	//TODO faire verrouiller vue de sélection quand on a commencé à faire un changement pour pas perdre les données

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

	public void updateStatus(String message) {

	}

}
