package com.weelgo.eclipse.plugin.undoredo;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weelgo.core.CoreUtils;
import com.weelgo.core.undoredo.UndoRedoNode;
import com.weelgo.eclipse.plugin.CMService;
import com.weelgo.eclipse.plugin.EventBroker;
import com.weelgo.eclipse.plugin.Factory;
import com.weelgo.eclipse.plugin.job.CMGoTSpecificUndoRedoJob;
import com.weelgo.eclipse.plugin.job.CMRedoJob;
import com.weelgo.eclipse.plugin.job.CMUndoJob;

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

	public void saveModel(String moduleUniqueidentifier, String label, String icon) {
		logger.debug("Save on UndoRedoService for " + moduleUniqueidentifier);
		cmService.getModulesManager().saveModelForUndoRedo(moduleUniqueidentifier,
				UndoRedoInfoData.create(label, icon));
	}

	public void saveAllModel(String label, String icon) {
		logger.debug("Save all models on UndoRedoService.");
		cmService.getModulesManager().saveAllModelsForUndoRedo(UndoRedoInfoData.create(label, icon));
	}

	public void restoreModel(String moduleUniqueidentifier) {
		logger.debug("Restore on UndoRedoService for " + moduleUniqueidentifier);
		cmService.getModulesManager().restoreModelForUndoRedo(moduleUniqueidentifier);
	}

	public void restoreAllModel() {
		logger.debug("Restore all models on UndoRedoService.");
		cmService.getModulesManager().restoreAllModelsForUndoRedo();
	}

	public void goToSpecificUndoRedoSave(String moduleUniqueidentifier, UndoRedoNode node) {
		logger.debug("Go to specific undo/redo node for " + moduleUniqueidentifier);
		CMGoTSpecificUndoRedoJob j = Factory.create(CMGoTSpecificUndoRedoJob.class);
		j.setModuleUniqueIdentifier(moduleUniqueidentifier);
		j.setNode(node);
		j.doSchedule();
	}

	public void undoModel(String moduleUniqueidentifier) {
		logger.debug("Undo on UndoRedoService for " + moduleUniqueidentifier);
		CMUndoJob j = Factory.create(CMUndoJob.class);
		j.setModuleUniqueIdentifier(moduleUniqueidentifier);
		j.doSchedule();
	}

	public void redoModel(String moduleUniqueidentifier) {
		logger.debug("Redo on UndoRedoService for " + moduleUniqueidentifier);
		CMRedoJob j = Factory.create(CMRedoJob.class);
		j.setModuleUniqueIdentifier(moduleUniqueidentifier);
		j.doSchedule();
	}

	public boolean canUndo(String moduleUniqueidentifier) {
		return CoreUtils.isNotNullOrEmpty(moduleUniqueidentifier);
	}

	public boolean canRedo(String moduleUniqueidentifier) {
		return CoreUtils.isNotNullOrEmpty(moduleUniqueidentifier);
	}

}
