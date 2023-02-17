package com.weelgo.eclipse.plugin;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weelgo.chainmapping.core.CMFileSystemDataSource;
import com.weelgo.chainmapping.core.CMModuleService;
import com.weelgo.chainmapping.core.CMModulesManager;
import com.weelgo.core.IProgressMonitor;

@Creatable
@Singleton
public class CMService {

	private static Logger logger = LoggerFactory.getLogger(CMService.class);
	private CMFileSystemDataSource workspaceDataSource;
	private CMModulesManager modulesManager;

	public void createModule(IProgressMonitor progressMonitor, Object moduleParentFolderPath, String moduleName,
			String modulePackageName) {

		modulesManager.createModule(progressMonitor, workspaceDataSource, moduleParentFolderPath, moduleName,
				modulePackageName);
	}

	public void saveAllModules(IProgressMonitor monitor) {
		modulesManager.saveAll(monitor);
	}

	public void loadModules(IProgressMonitor monitor) {
		modulesManager.load(monitor);
	}

	public CMModulesManager getModulesManager() {
		return modulesManager;
	}

	@PostConstruct
	public void postConstruct() {
		modulesManager = new CMModulesManager() {

			@Override
			public CMFileSystemDataSource createCMFileSystemDataSource() {
				CMFileSystemDataSource ds = new CMFileSystemDataSource() {

					@Override
					public String isMarkeableToSHow(Object elem) {

						Object parent = getHierarchicalTreeSystemProvider().getParentFolder(elem);
						if (getHierarchicalTreeSystemProvider().isSameFolder(parent, getRootFolder())) {
							return getHierarchicalTreeSystemProvider().getUniqueIdForFolderOrFile(elem);
						}
						return null;
					}

				};
				ds.setHierarchicalTreeSystemProvider(new EclipseHierarchicalTreeSystemProvider());
				return ds;
			}
		};

		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		workspaceDataSource = modulesManager.createCMFileSystemDataSource();
		workspaceDataSource.setRootFolder(root);

		modulesManager.getSources().add(workspaceDataSource);
	}

	public String findModuleUniqueIdentifierId(Object o) {
		return getModulesManager().findModuleUniqueIdentifierId(o);
	}
	public CMModuleService findModuleService(Object o) {
		return getModulesManager().findModuleService(o);
	}

	public boolean isModulePackageFreeForEclipseWorkspace(IProgressMonitor progressMonitor, Object moduleParentFolder,
			String packageName) {
		return getModulesManager().isModulePackageFree(progressMonitor, workspaceDataSource, moduleParentFolder,
				packageName);
	}	
}
