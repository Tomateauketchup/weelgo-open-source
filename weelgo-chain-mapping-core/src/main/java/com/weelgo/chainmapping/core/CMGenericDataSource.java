package com.weelgo.chainmapping.core;

import java.util.List;
import java.util.Map;

import com.weelgo.core.IProgressMonitor;

public abstract class CMGenericDataSource {

	public abstract void load(IProgressMonitor progressMonitor, Map<String, CMModuleService> servicesMap);

	public abstract void save(IProgressMonitor progressMonitor, CMModuleService moduleService);

	public abstract List<Object> getContainers();

	private HierarchicalTreeSystemProvider hierarchicalTreeSystemProvider = new FileSystemProvider();

	public HierarchicalTreeSystemProvider getHierarchicalTreeSystemProvider() {
		return hierarchicalTreeSystemProvider;
	}

	public void setHierarchicalTreeSystemProvider(HierarchicalTreeSystemProvider hierarchicalTreeSystemProvider) {
		this.hierarchicalTreeSystemProvider = hierarchicalTreeSystemProvider;
	}

	public abstract boolean isMine(Object o);

	public boolean isHiddenElement(Object o) {
		return false;
	}

	public abstract boolean isModulePackageFree(IProgressMonitor progressMonitor, Object moduleRootFolder,
			String packageName);

	public abstract void createModule(IProgressMonitor progressMonitor, Object moduleRootFolder,
			CMModuleService service);
}
