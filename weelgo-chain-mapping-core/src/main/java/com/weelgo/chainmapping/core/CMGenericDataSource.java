package com.weelgo.chainmapping.core;

import static com.weelgo.core.CoreUtils.getPackages;
import static com.weelgo.core.CoreUtils.isNotNullOrEmpty;

import java.util.List;
import java.util.Map;

import com.weelgo.core.IProgressMonitor;

public abstract class CMGenericDataSource {

	public abstract void load(IProgressMonitor progressMonitor, Map<String, CMModuleService> servicesMap);

	public abstract void load(IProgressMonitor progressMonitor, CMModuleService servicesMap);

	public abstract void save(IProgressMonitor progressMonitor, CMModuleService moduleService);

	public abstract List<Object> getContainers();

	private HierarchicalTreeSystemProvider hierarchicalTreeSystemProvider = new FileSystemProvider();

	public HierarchicalTreeSystemProvider getHierarchicalTreeSystemProvider() {
		return hierarchicalTreeSystemProvider;
	}

	public void setHierarchicalTreeSystemProvider(HierarchicalTreeSystemProvider hierarchicalTreeSystemProvider) {
		this.hierarchicalTreeSystemProvider = hierarchicalTreeSystemProvider;
	}

	public boolean isMine(Object o) {

		if (o instanceof CMModuleService) {
			CMModuleService ser = (CMModuleService) o;
			return isMine(ser.getParentContainer());
		}
		// TODO problème du isMine : Indique su la ressource est bien de ce file system.
		// Pour l'instant il n'y a que la workspace donc on retourne oui.
		return true;
	}
	
	public Object getFolderOfGroup(Object parentFolderOfTheModuleFolder, CMGroup group) {
		if (parentFolderOfTheModuleFolder != null && group != null) {
			String packageParentPath = group.getPackageFullPath();
			String[] parents = getPackages(packageParentPath);
			Object groupFolder = parentFolderOfTheModuleFolder;
			if (parents != null) {
				for (String str : parents) {
					if (isNotNullOrEmpty(str)) {
						groupFolder = getHierarchicalTreeSystemProvider().getFolder(groupFolder, str);
					}
				}
			}
			return groupFolder;
		}
		return null;
	}

	public boolean isHiddenElement(Object o) {
		return false;
	}

	public abstract boolean isModulePackageFree(IProgressMonitor progressMonitor, Object moduleRootFolder,
			String packageName);

	public abstract Object createModule(IProgressMonitor progressMonitor, Object moduleRootFolder,
			CMModuleService service);
}
