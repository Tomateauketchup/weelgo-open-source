package com.weelgo.eclipse.plugin.navigator;

import javax.inject.Inject;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.weelgo.chainmapping.core.CMDeliverable;
import com.weelgo.chainmapping.core.CMGenericDataSource;
import com.weelgo.chainmapping.core.CMGroup;
import com.weelgo.chainmapping.core.CMModuleService;
import com.weelgo.chainmapping.core.CMModulesManager;
import com.weelgo.chainmapping.core.CMTask;
import com.weelgo.core.CoreUtils;
import com.weelgo.core.INamedObject;
import com.weelgo.eclipse.plugin.CMService;
import com.weelgo.eclipse.plugin.ImagesFactory;

public class TreeLabelProvider extends LabelProvider {

	@Inject
	private CMService cmServices;

	public CMService getCmServices() {
		return cmServices;
	}

	public void setCmServices(CMService cmServices) {
		this.cmServices = cmServices;
	}

	@Override
	public String getText(Object element) {

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

		if (gp != null) {
			return gp.getPackageName();
		} else if (modService != null) {
			return modService.getName();
		} else if (element instanceof INamedObject) {
			return ((INamedObject) element).getName();
		} else if (modManager != null) {
			return "Modules manager";
		} else {
			return getModulesManager().getName(element);
		}
	}

	@Override
	public Image getImage(Object element) {

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

		if (modService == null) {
			CMGenericDataSource e = getModulesManager().isRootContainerOfDataSource(element);
			if (e != null) {
				return ImagesFactory.getIconImage(ImagesFactory.GROUP_PROJECT);
			}
		}

		if (modService != null) {
			return ImagesFactory.getIconImage(ImagesFactory.CHAIN_MAPPING_ICON);
		} else if (gp != null) {
			return ImagesFactory.getIconImage(ImagesFactory.GROUP_ICON);
		} else if (tsk != null) {
			return ImagesFactory.getIconImage(ImagesFactory.TASK_ICON);
		} else if (del != null) {
			return ImagesFactory.getIconImage(ImagesFactory.DELIVERABLE_ICON);
		} else if (getModulesManager().isFile(element)) {
			return ImagesFactory.getIconImage(ImagesFactory.FILE_ICON);
		} else if (getModulesManager().isFolder(element)) {
			return ImagesFactory.getIconImage(ImagesFactory.FOLDER_ICON);
		}

		return super.getImage(element);
	}

	public CMModuleService isModuleService(Object element) {
		return TreeContentProvider.isModuleService(element);
	}

	public CMModulesManager isModulesManager(Object element) {
		return TreeContentProvider.isModulesManager(element);
	}

	public static CMGenericDataSource isDataSource(Object element) {
		return TreeContentProvider.isDataSource(element);
	}

	public CMGroup isGroup(Object element) {
		return TreeContentProvider.isGroup(element);
	}

	public CMTask isTask(Object element) {
		return TreeContentProvider.isTask(element);
	}

	public CMDeliverable isDeliverable(Object element) {
		return TreeContentProvider.isDeliverable(element);
	}

	public CMModulesManager getModulesManager() {
		return getCmServices().getModulesManager();
	}
}
