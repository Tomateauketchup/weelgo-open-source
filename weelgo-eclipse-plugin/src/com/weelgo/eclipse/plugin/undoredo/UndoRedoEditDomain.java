package com.weelgo.eclipse.plugin.undoredo;

import javax.inject.Inject;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.KeyHandler;
import org.eclipse.gef.KeyStroke;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;

import com.weelgo.chainmapping.core.IModuleUniqueIdentifierObject;
import com.weelgo.core.undoredo.UndoRedoNode;
import com.weelgo.eclipse.plugin.CMService;
import com.weelgo.eclipse.plugin.CurrentSelectionService;
import com.weelgo.eclipse.plugin.KeyHelper;
import com.weelgo.eclipse.plugin.handlers.SaveHandler;

@Creatable
public class UndoRedoEditDomain extends EditDomain {

	private NodeEditPart mouseMoveEditPart;

	@Inject
	ESelectionService selectionService;

	@Inject
	CurrentSelectionService currentSelectionService;

	@Inject
	UndoRedoService undoRedoService;

	@Inject
	CMService cmService;

	@Override
	public void mouseUp(MouseEvent mouseEvent, EditPartViewer viewer) {
		EditPart e = findEditPart(mouseEvent, viewer);
		selectionService.setSelection(e);
		super.mouseUp(mouseEvent, viewer);
	}

	@Override
	public void mouseDoubleClick(MouseEvent mouseEvent, EditPartViewer viewer) {
		EditPart e = findEditPart(mouseEvent, viewer);
		selectionService.setSelection(e);
		UndoRedoNode node = currentSelectionService.find(UndoRedoNode.class);
		IModuleUniqueIdentifierObject obj = currentSelectionService.find(IModuleUniqueIdentifierObject.class);
		if (node != null && obj != null) {
			undoRedoService.goToSpecificUndoRedoSave(obj.getModuleUniqueIdentifier(), node);
		}

		super.mouseDoubleClick(mouseEvent, viewer);
	}

	@Override
	public void keyDown(KeyEvent keyEvent, EditPartViewer viewer) {
		if (KeyHelper.isCTRL_Z(keyEvent)) {
			undoRedoService.undoModel(currentSelectionService.findModuleUniqueIdentifierObjectId());
		}
		if (KeyHelper.isCTRL_Y(keyEvent)) {
			undoRedoService.redoModel(currentSelectionService.findModuleUniqueIdentifierObjectId());
		}
		if (KeyHelper.isCTRL_S(keyEvent)) {
			SaveHandler sh = new SaveHandler();
			sh.execute(cmService, currentSelectionService);
		}
		super.keyUp(keyEvent, viewer);
	}

	@Override
	public void mouseMove(MouseEvent mouseEvent, EditPartViewer viewer) {
		EditPart e = findEditPart(mouseEvent, viewer);
		if (e instanceof NodeEditPart node) {
			if (node.equals(mouseMoveEditPart) == false) {
				if (mouseMoveEditPart != null) {
					mouseMoveEditPart.setMouseOver(false);
					mouseMoveEditPart.refreshVisuals();
				}
				node.setMouseOver(true);
				mouseMoveEditPart = node;
				mouseMoveEditPart.refreshVisuals();
			}

		} else {
			if (mouseMoveEditPart != null) {
				mouseMoveEditPart.setMouseOver(false);
				mouseMoveEditPart.refreshVisuals();
			}
			mouseMoveEditPart = null;
		}
		super.mouseMove(mouseEvent, viewer);
	}

	@Override
	public void mouseHover(MouseEvent mouseEvent, EditPartViewer viewer) {
		super.mouseHover(mouseEvent, viewer);
	}

	public EditPart findEditPart(MouseEvent mouseEvent, EditPartViewer viewer) {
		return viewer.findObjectAt(new Point(mouseEvent.x, mouseEvent.y));
	}
}
