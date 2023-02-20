package com.weelgo.eclipse.plugin;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.RootEditPart;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

import com.weelgo.chainmapping.core.IModuleUniqueIdentifierObject;
import com.weelgo.core.CoreUtils;
import com.weelgo.core.undoredo.UndoRedoNode;
import com.weelgo.eclipse.plugin.undoredo.NodeModel;

@Creatable
@Singleton
public class SelectionAdapter {

	@Inject
	CMService cmService;

	public IModuleUniqueIdentifierObject findModuleUniqueIdentifierObject(Object objectToCheck) {
		return find(objectToCheck, IModuleUniqueIdentifierObject.class);
	}

	public String findModuleUniqueIdentifierObjectId(Object objectToCheck) {
		IModuleUniqueIdentifierObject ser = findModuleUniqueIdentifierObject(objectToCheck);
		if (ser != null) {
			return ser.getModuleUniqueIdentifier();
		}
		return "";
	}

	public <T> T find(Object objectToCheck, Class<T> wantedClass) {

		if (CoreUtils.isInstanceOf(objectToCheck, wantedClass)) {
			return (T) objectToCheck;
		}

		if (objectToCheck instanceof ISelection) {
			if (objectToCheck instanceof IStructuredSelection s) {

				Object fst = s.getFirstElement();

				T o = find(fst, wantedClass);
				if (o != null) {
					return o;
				}

			}
		}

		if (objectToCheck instanceof NodeModel nm) {

			UndoRedoNode undoRedoNode = nm.getUndoRedoNode();

			T o = find(undoRedoNode, wantedClass);
			if (o != null) {
				return o;
			}
		}

		if (objectToCheck instanceof EditPart part) {
			Object mod = part.getModel();
			T o = find(mod, wantedClass);
			if (o != null) {
				return o;
			}
		}

		if (objectToCheck instanceof RootEditPart part) {
			EditPart pt = part.getContents();
			T o = find(pt, wantedClass);
			if (o != null) {
				return o;
			}

		}

		// Dans le cas de la recherche d'un objet de ce type, on fait une recherche plus
		// large
		if (wantedClass.equals(IModuleUniqueIdentifierObject.class)) {
			return (T) cmService.findModuleService(objectToCheck);
		}

		return null;
	}

}
