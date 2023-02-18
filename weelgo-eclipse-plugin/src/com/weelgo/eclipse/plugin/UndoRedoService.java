package com.weelgo.eclipse.plugin;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.osgi.service.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weelgo.core.CoreUtils;

@Creatable
@Singleton
public class UndoRedoService {

	private static Logger logger = LoggerFactory.getLogger(UndoRedoService.class);

	@Inject
	private CMService cmService;

	@Inject
	private EventBroker eventBroker;

	@PostConstruct
	public void postConstruct() {

	}

	public void saveModel(String moduleUniqueidentifier) {
		logger.debug("Save on UndoRedoService for " + moduleUniqueidentifier);
		cmService.getModulesManager().saveModelForUndoRedo(moduleUniqueidentifier);
	}

	public void saveAllModel() {
		logger.debug("Save all models on UndoRedoService.");
		cmService.getModulesManager().saveAllModelsForUndoRedo();
	}

	public void restoreModel(String moduleUniqueidentifier) {
		logger.debug("Restore on UndoRedoService for " + moduleUniqueidentifier);
		cmService.getModulesManager().restoreModelForUndoRedo(moduleUniqueidentifier);
	}

	public void restoreAllModel() {
		logger.debug("Restore all models on UndoRedoService.");
		cmService.getModulesManager().restoreAllModelsForUndoRedo();
	}

	public void undoModel(String moduleUniqueidentifier) {
		logger.debug("Undo on UndoRedoService for " + moduleUniqueidentifier);
		cmService.getModulesManager().undoModelForUndoRedo(moduleUniqueidentifier);
		eventBroker.sentEvent(CMEvents.MODULE_UNDO_REDO_OPERATION_DONE, moduleUniqueidentifier);
	}

	public void redoModel(String moduleUniqueidentifier) {
		logger.debug("Redo on UndoRedoService for " + moduleUniqueidentifier);
		cmService.getModulesManager().redoModelForUndoRedo(moduleUniqueidentifier);
		eventBroker.sentEvent(CMEvents.MODULE_UNDO_REDO_OPERATION_DONE, moduleUniqueidentifier);
	}

	public boolean canUndo(String moduleUniqueidentifier) {
		return CoreUtils.isNotNullOrEmpty(moduleUniqueidentifier);
	}

	public boolean canRedo(String moduleUniqueidentifier) {
		return CoreUtils.isNotNullOrEmpty(moduleUniqueidentifier);
	}

}
