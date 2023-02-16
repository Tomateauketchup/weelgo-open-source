package com.weelgo.eclipse.plugin;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.services.IServiceConstants;

@Creatable
@Singleton
public class CurrentSelectionService {	

	private Object currentSelection;

	@Inject
	public void setTasks(@Optional @Named(IServiceConstants.ACTIVE_SELECTION) Object selection) {
		currentSelection = selection;
	}

	public Object getCurrentSelection() {
		return currentSelection;
	}	

}
