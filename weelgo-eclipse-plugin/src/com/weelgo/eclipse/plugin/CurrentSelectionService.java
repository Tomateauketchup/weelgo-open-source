package com.weelgo.eclipse.plugin;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.services.IServiceConstants;

import com.weelgo.chainmapping.core.IDataSourceObject;
import com.weelgo.chainmapping.core.IModuleUniqueIdentifierObject;
import com.weelgo.eclipse.plugin.chainmapping.editor.NodeEditPart;

@Creatable
@Singleton
public class CurrentSelectionService {

	@Inject
	EventBroker eventBroker;

	@Inject
	SelectionAdapter selectionAdapter;

	private Object currentSelection;

	@Inject
	public void setSelectionChanged(@Optional @Named(IServiceConstants.ACTIVE_SELECTION) Object selection) {
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

	public IDataSourceObject findDataSourceObject(Object objectToCheck) {
		return selectionAdapter.findDataSourceObject(objectToCheck);
	}

	public String findDataSourceUuid(Object objectToCheck) {
		return selectionAdapter.findDataSourceUuid(objectToCheck);
	}

	public  <T> List<T> findList(Class<T> c) {
		return selectionAdapter.findList(currentSelection, c);
	}
}
