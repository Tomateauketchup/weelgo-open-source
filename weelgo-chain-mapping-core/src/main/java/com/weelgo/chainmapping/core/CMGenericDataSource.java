package com.weelgo.chainmapping.core;

import static com.weelgo.core.CoreUtils.getPackages;
import static com.weelgo.core.CoreUtils.isNotNullOrEmpty;

import java.util.List;
import java.util.Map;

import com.weelgo.core.INamedObject;
import com.weelgo.core.IProgressMonitor;
import com.weelgo.core.IUuidObject;

public abstract class CMGenericDataSource implements IUuidObject, INamedObject {

	private String uuid;
	private String name;
	private HierarchicalTreeSystemProvider hierarchicalTreeSystemProvider = new FileSystemProvider();

	public abstract void load(IProgressMonitor progressMonitor, Map<String, CMModuleService> servicesMap);

	public abstract void load(IProgressMonitor progressMonitor, CMModuleService servicesMap);

	public abstract void save(IProgressMonitor progressMonitor, CMModuleService moduleService);

	public abstract List<Object> getContainers();

	public HierarchicalTreeSystemProvider getHierarchicalTreeSystemProvider() {
		return hierarchicalTreeSystemProvider;
	}

	public void setHierarchicalTreeSystemProvider(HierarchicalTreeSystemProvider hierarchicalTreeSystemProvider) {
		this.hierarchicalTreeSystemProvider = hierarchicalTreeSystemProvider;
	}

	public boolean isMine(Object o) {

		if (o instanceof CMModuleService) {
			CMModuleService ser = (CMModuleService) o;
			return isMine(ser.getContainer());
		}
		// TODO problème du isMine : Indique su la ressource est bien de ce file system.
		// Pour l'instant il n'y a que la workspace donc on retourne oui.
		return true;
	}

	public Object getFolderOfGroup(CMModuleService ser, CMGroup group) {
		if (ser != null && group != null) {
			Object moduleFolder = ser.getContainer();
			Object parentOfModule = getHierarchicalTreeSystemProvider().getParentFolder(moduleFolder);
			return getFolderOfGroup(parentOfModule, group);
		}
		return null;
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

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
