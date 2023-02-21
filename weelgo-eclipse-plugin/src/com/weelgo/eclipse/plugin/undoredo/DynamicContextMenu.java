package com.weelgo.eclipse.plugin.undoredo;

import java.util.List;

import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;

import com.weelgo.core.undoredo.UndoRedoNode;
import com.weelgo.eclipse.plugin.CurrentSelectionService;
import com.weelgo.eclipse.plugin.Factory;
import com.weelgo.eclipse.plugin.ImagesFactory;
import com.weelgo.eclipse.plugin.handlers.GoToUndoRedoSaveHandler;

public class DynamicContextMenu {

	@AboutToShow
	public void aboutToShow(List<MMenuElement> items, CurrentSelectionService currentSelectionService) {

		if (currentSelectionService.find(UndoRedoNode.class) != null) {

			NodeModel node = currentSelectionService.find(NodeModel.class);
			if (node != null && node.isCurrentNode()) {
				MHandledMenuItem it = Factory.createMHandledMenuItem("Save", ImagesFactory.SAVE_ICON,
						Factory.COMMAND_SAVE_ID);
				items.add(it);
			} else {
				MDirectMenuItem item = Factory.createMDirectMenuItem("Go to this undo", ImagesFactory.ARROW_LEFT,
						GoToUndoRedoSaveHandler.class);
				items.add(item);
//				item = Factory.createMDirectMenuItem("Go and save", ImagesFactory.SAVE_ICON, GoToAndSaveHandler.class);
//				items.add(item);
			}

		}
	}

}
