package com.weelgo.eclipse.plugin.handlers;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;

import com.weelgo.eclipse.plugin.CMService;
import com.weelgo.eclipse.plugin.CurrentSelectionService;
import com.weelgo.eclipse.plugin.undoredo.UndoRedoService;

public class GoToAndSaveHandler {
	@Execute
	public void execute(CMService cmServices, UndoRedoService undoRedoService,
			CurrentSelectionService currentSelectionService, GoToUndoRedoSaveHandler hand1, SaveHandler saveHandler) {
		hand1.execute(undoRedoService, currentSelectionService);
		saveHandler.execute(cmServices, currentSelectionService);

	}

	@CanExecute
	public boolean canExecute() {
		return true;
	}
}