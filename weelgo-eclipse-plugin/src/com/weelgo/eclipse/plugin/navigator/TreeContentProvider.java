package com.weelgo.eclipse.plugin.navigator;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.jface.viewers.ITreeContentProvider;

import com.weelgo.chainmapping.core.CMDeliverable;
import com.weelgo.chainmapping.core.CMGenericDataSource;
import com.weelgo.chainmapping.core.CMGroup;
import com.weelgo.chainmapping.core.CMModuleService;
import com.weelgo.chainmapping.core.CMModulesManager;
import com.weelgo.chainmapping.core.CMReturnObj;
import com.weelgo.chainmapping.core.CMTask;
import com.weelgo.core.CoreUtils;
import com.weelgo.core.IUuidObject;
import com.weelgo.eclipse.plugin.CMService;

public class TreeContentProvider implements ITreeContentProvider {

	@Inject
	private CMService cmServices;

	@Override
	public Object[] getChildren(Object element) {

		CMModulesManager modManager = isModulesManager(element);
		CMGenericDataSource dataSource = isDataSource(element);
		CMModuleService modService = isModuleService(element);
		CMGroup gp = isGroup(element);
		CMTask tsk = isTask(element);
		CMDeliverable del = isDeliverable(element);

		if (CoreUtils.isAllNull(modManager, dataSource, modService, gp, tsk, del)) {
			// We look if the element is a matching module
			modService = getModulesManager().getCorrespondingModuleServiceOfFolder(element);
		}

		ArrayList<Object> arl = new ArrayList<>();

		if (modManager != null) {
			CoreUtils.putListIntoList(modManager.getChildFolders(), arl);
		} else if (dataSource != null) {
			CoreUtils.putListIntoList(dataSource.getContainers(), arl);
		} else if (gp != null) {
			CoreUtils.putListIntoList(getModulesManager().getChildsForTreeNavigator(gp), arl);
		} else if (modService != null) {
			if (modService.getRootGroup() != null) {
				arl.add(modService.getRootGroup());
			}
		} else {
			CoreUtils.putListIntoList(getModulesManager().getChildFolders(element), arl);
		}

		arl.removeIf(t -> getModulesManager().isHiddenElement(t));

		return arl.toArray(new Object[arl.size()]);

	}

	@Override
	public Object getParent(Object element) {

		CMModulesManager modManager = isModulesManager(element);
		CMModuleService modService = isModuleService(element);
		CMGenericDataSource dataSource = isDataSource(element);
		CMGroup gp = isGroup(element);
		CMTask tsk = isTask(element);
		CMDeliverable del = isDeliverable(element);

		if (CoreUtils.isAllNull(modManager, dataSource, modService, gp, tsk, del)) {
			// We look if the element is a matching module
			modService = getModulesManager().getCorrespondingModuleServiceOfFolder(element);
		}

		if (modService == null) {
			CMGenericDataSource e = getModulesManager().isRootContainerOfDataSource(element);
			if (e != null) {
				return null;
			}
		}

		if (modManager != null) {
			return null;
		}
		if (dataSource != null) {
			return getModulesManager();
		}
		if (modService != null) {
			return getModulesManager().getParentForTreeNavigator(modService);			
		}

		String gpUuid = "";
		if (gp != null) {
			gpUuid = gp.getGroupUuid();
		} else if (tsk != null) {
			gpUuid = tsk.getGroupUuid();
		} else if (del != null) {
			gpUuid = del.getGroupUuid();
		}

		CMReturnObj ret = getObjectByUuid(gpUuid);
		Object obj = CMReturnObj.getObject(ret);
		if (obj != null) {
			return obj;
		}

		// If still null, we look with manager
		return getModulesManager().getParentFolder(element);

	}

	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	@Override
	public boolean hasChildren(Object element) {

		Object[] children = getChildren(element);
		return children != null && children.length > 0;

	}

	public CMReturnObj getObjectByUuid(String uuid) {
		if (getModulesManager() != null) {
			return getModulesManager().getObjectByUuid(uuid);
		}
		return null;
	}

	public CMReturnObj getObjectByUuid(IUuidObject o) {
		if (getModulesManager() != null) {
			return getModulesManager().getObjectByUuid(o);
		}
		return null;
	}

	public static CMModuleService isModuleService(Object element) {
		if (element instanceof CMModuleService) {
			return (CMModuleService) element;
		}
		return null;
	}

	public static CMModulesManager isModulesManager(Object element) {
		if (element instanceof CMModulesManager) {
			return (CMModulesManager) element;
		}
		return null;
	}

	public static CMGenericDataSource isDataSource(Object element) {
		if (element instanceof CMGenericDataSource) {
			return (CMGenericDataSource) element;
		}
		return null;
	}

	public static CMGroup isGroup(Object element) {
		if (element instanceof CMGroup) {
			return (CMGroup) element;
		}
		return null;
	}

	public static CMTask isTask(Object element) {
		if (element instanceof CMTask) {
			return (CMTask) element;
		}
		return null;
	}

	public static CMDeliverable isDeliverable(Object element) {
		if (element instanceof CMDeliverable) {
			return (CMDeliverable) element;
		}
		return null;
	}

	public CMService getCmServices() {
		return cmServices;
	}

	public void setCmServices(CMService cmServices) {
		this.cmServices = cmServices;
	}

	public CMModulesManager getModulesManager() {
		return getCmServices().getModulesManager();
	}

}