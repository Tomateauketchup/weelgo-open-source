package com.weelgo.eclipse.plugin.handlers;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.di.annotations.Execute;

import com.weelgo.chainmapping.core.IModuleUniqueIdentifierObject;
import com.weelgo.core.undoredo.UndoRedoNode;
import com.weelgo.eclipse.plugin.CurrentSelectionService;
import com.weelgo.eclipse.plugin.undoredo.UndoRedoService;

@Creatable
public class GoToUndoRedoSaveHandler {

	@Execute
	public void execute(UndoRedoService undoRedoService, CurrentSelectionService currentSelectionService) {

		UndoRedoNode node = currentSelectionService.find(UndoRedoNode.class);
		IModuleUniqueIdentifierObject obj = currentSelectionService.find(IModuleUniqueIdentifierObject.class);

		if (node != null && obj != null) {
			undoRedoService.goToSpecificUndoRedoSave(obj.getModuleUniqueIdentifier(), node);
		}

	}

	@CanExecute
	public boolean canExecute() {
		return true;
	}

}
