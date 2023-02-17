package com.weelgo.eclipse.plugin.handlers;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;

import com.weelgo.eclipse.plugin.CMService;
import com.weelgo.eclipse.plugin.CurrentSelectionService;
import com.weelgo.eclipse.plugin.UndoRedoService;

public class RedoHandler {

	@Execute
	public void execute(UndoRedoService undoRedoService, CMService cmService,
			CurrentSelectionService currentSelectionService) {
		String str = cmService.findModuleUniqueIdentifierId(currentSelectionService.getCurrentSelection());
		undoRedoService.redoModel(str);
	}

	@CanExecute
	public boolean canExecute(UndoRedoService undoRedoService, CMService cmService,
			CurrentSelectionService currentSelectionService) {
		String str = cmService.findModuleUniqueIdentifierId(currentSelectionService.getCurrentSelection());
		return undoRedoService.canRedo(str);
	}

}
