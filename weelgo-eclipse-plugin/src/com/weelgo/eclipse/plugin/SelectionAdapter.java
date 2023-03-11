package com.weelgo.eclipse.plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.RootEditPart;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

import com.weelgo.chainmapping.core.CMModuleService;
import com.weelgo.chainmapping.core.IDataSourceObject;
import com.weelgo.chainmapping.core.IModuleUniqueIdentifierObject;
import com.weelgo.chainmapping.core.navigator.NavNode;
import com.weelgo.core.CoreUtils;
import com.weelgo.core.IUuidObject;
import com.weelgo.core.undoredo.UndoRedoNode;
import com.weelgo.eclipse.plugin.undoredo.NodeModel;

@Creatable
@Singleton
public class SelectionAdapter {

	@Inject
	CMService cmService;

	public CMModuleService findModuleService(Object objectToCheck) {
		IModuleUniqueIdentifierObject o = findModuleUniqueIdentifierObject(objectToCheck);
		if (o != null) {
			if (o instanceof CMModuleService) {
				return (CMModuleService) o;
			}
			return cmService.findModuleService(o);
		}
		return null;
	}

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

	public IDataSourceObject findDataSourceObject(Object objectToCheck) {
		return find(objectToCheck, IDataSourceObject.class);
	}

	public String findDataSourceUuid(Object objectToCheck) {
		IDataSourceObject ser = findDataSourceObject(objectToCheck);
		if (ser != null) {
			return ser.getDataSourceUuid();
		}
		return "";
	}

	public List findList(Object objectToCheck) {
		return find(objectToCheck, List.class);
	}

	public List findListMulti(Object objectToCheck, Class... wantedClass) {
		List lst = new ArrayList<>();
		if (wantedClass != null) {
			for (Class c : wantedClass) {
				CoreUtils.putListIntoList(findList(objectToCheck, c), lst);
			}
		}
		return lst;

	}

	public <T> List<T> findList(Object objectToCheck, Class<T> wantedClass) {
		List<T> arl = new ArrayList<T>();

		List listToCheck = null;

		if (objectToCheck instanceof ISelection) {
			if (objectToCheck instanceof IStructuredSelection s) {

				listToCheck = s.toList();

			}
		} else if (objectToCheck instanceof List) {
			listToCheck = (List) objectToCheck;
		}

		if (listToCheck != null) {
			for (Object ob : listToCheck) {
				T o = find(ob, wantedClass);
				if (o != null) {
					arl.add(o);
				}
			}
		}else
		{
			T o = find(objectToCheck, wantedClass);
			if (o != null) {
				arl.add(o);
			}
		}

		if (arl.size() > 0) {
			return arl;
		}
		return null;
	}

	public <T> T findUuidObject(Object objectToCheck, String uuid) {
		IUuidObject o = find(objectToCheck, IUuidObject.class);
		if (o != null && CoreUtils.isStrictlyEqualsString(o.getUuid(), uuid)) {
			return (T) o;
		}

		Map map = find(objectToCheck, Map.class);
		if (map != null && map.size() > 0) {
			Object key = map.keySet().iterator().next();
			Object val = map.values().iterator().next();
			if (key instanceof String && val instanceof IUuidObject) {
				return (T) map.get(uuid);
			}
		}
		return null;
	}

	public <T> T find(Object objectToCheck, Class<T> wantedClass) {

		if (CoreUtils.isInstanceOf(objectToCheck, wantedClass)) {
			return (T) objectToCheck;
		}

		if (objectToCheck instanceof List lst) {
			for (Object ob : lst) {
				T o = find(ob, wantedClass);
				if (o != null) {
					return o;
				}
			}
		}

		if (objectToCheck instanceof ISelection) {
			if (objectToCheck instanceof IStructuredSelection s) {

				List listToCheck = s.toList();

				T o = find(listToCheck, wantedClass);
				if (o != null) {
					return o;
				}

			}
		}

		if (objectToCheck instanceof CMModuleService n) {
			Object data = n.getRootGroup();
			T o = find(data, wantedClass);
			if (o != null) {
				return o;
			}

			data = n.getContainer();
			o = find(data, wantedClass);
			if (o != null) {
				return o;
			}
		}

		if (objectToCheck instanceof NavNode n) {
			Object data = n.getData();
			T o = find(data, wantedClass);
			if (o != null) {
				return o;
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
