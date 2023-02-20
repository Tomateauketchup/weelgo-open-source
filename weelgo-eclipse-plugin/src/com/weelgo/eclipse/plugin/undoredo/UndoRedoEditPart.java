package com.weelgo.eclipse.plugin.undoredo;

import java.util.List;

import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import com.weelgo.chainmapping.core.IModuleUniqueIdentifierObject;

public class UndoRedoEditPart extends AbstractGraphicalEditPart implements IModuleUniqueIdentifierObject {

	@Override
	protected IFigure createFigure() {
		FreeformLayer layer = new FreeformLayer();
		layer.setLayoutManager(new FreeformLayout());
		return layer;
	}

	@Override
	protected void createEditPolicies() {
	}

	@Override
	public void refresh() {
		getUndoRedoModel().updateNodes();
		super.refresh();
	}

	@Override
	protected void refreshVisuals() {
		List<EditPart> childs = getChildren();
		if(childs!=null)
		{
			childs.forEach(t -> t.refresh());
		}
		super.refreshVisuals();
	}

	@Override
	protected List getModelChildren() {

		return getUndoRedoModel().getNodes();
	}

	public UndoRedoModel getUndoRedoModel() {
		return (UndoRedoModel) getModel();
	}

	@Override
	public String getModuleUniqueIdentifier() {
		UndoRedoModel mod = getUndoRedoModel();
		if (mod != null) {
			return mod.getModuleUniqueIdentifier();
		}
		return "";
	}

	@Override
	public void setModuleUniqueIdentifier(String arg0) {

	}
}
