package com.weelgo.eclipse.plugin;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.services.IServiceConstants;

import com.weelgo.chainmapping.core.IModuleUniqueIdentifierObject;

@Creatable
@Singleton
public class CurrentSelectionService {

	@Inject
	EventBroker eventBroker;

	@Inject
	SelectionAdapter selectionAdapter;

	private Object currentSelection;

	@Inject
	public void setTasks(@Optional @Named(IServiceConstants.ACTIVE_SELECTION) Object selection) {
		currentSelection = selection;
		eventBroker.sentEvent(CMEvents.SELECTION_CHANGED, selection);

	}

	public Object getCurrentSelection() {
		return currentSelection;
	}

	public <T> T find(Class<T> wantedClass) {
		return selectionAdapter.find(currentSelection, wantedClass);
	}

	public IModuleUniqueIdentifierObject findModuleUniqueIdentifierObject() {
		return selectionAdapter.findModuleUniqueIdentifierObject(currentSelection);
	}

	public String findModuleUniqueIdentifierObjectId() {
		return selectionAdapter.findModuleUniqueIdentifierObjectId(currentSelection);
	}
}
