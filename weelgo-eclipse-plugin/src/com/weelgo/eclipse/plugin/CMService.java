package com.weelgo.eclipse.plugin;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weelgo.chainmapping.core.CMFileSystemDataSource;
import com.weelgo.chainmapping.core.CMGroup;
import com.weelgo.chainmapping.core.CMModuleService;
import com.weelgo.chainmapping.core.CMModulesManager;
import com.weelgo.core.IProgressMonitor;

@Creatable
@Singleton
public class CMService {

	public static final String ECLIPSE_WORKSPACE_DATA_SOURCE_UUID = "eclipse_workspace_data_source";
	private static Logger logger = LoggerFactory.getLogger(CMService.class);
	private CMModulesManager modulesManager;

	public String createModule(IProgressMonitor progressMonitor, Object moduleParentFolderPath, String moduleName,
			String modulePackageName, String dataSourceUuid) {

		return modulesManager.createModule(progressMonitor, moduleParentFolderPath, moduleName, modulePackageName,
				dataSourceUuid);
	}

	public void saveAllModules(IProgressMonitor monitor) {
		modulesManager.saveAll(monitor);
	}

	public void loadModules(IProgressMonitor monitor) {
		modulesManager.loadAllModules(monitor);
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
		CMFileSystemDataSource workspaceDataSource = modulesManager.createCMFileSystemDataSource();
		workspaceDataSource.setName("Workspace");
		workspaceDataSource.setUuid(ECLIPSE_WORKSPACE_DATA_SOURCE_UUID);
		workspaceDataSource.setRootFolder(root);

		modulesManager.addDataSource(workspaceDataSource);
	}

	public String findModuleUniqueIdentifierId(Object o) {
		return getModulesManager().findModuleUniqueIdentifierId(o);
	}

	public CMModuleService findModuleService(Object o) {
		return getModulesManager().findModuleService(o);
	}

	public boolean isModulePackageFreeForEclipseWorkspace(IProgressMonitor progressMonitor, String datasourceUUid,
			Object moduleParentFolder, String packageName) {
		return getModulesManager().isModulePackageFree(progressMonitor, datasourceUUid, moduleParentFolder,
				packageName);
	}

	public String getFolderFullPathOfGroup(CMGroup gp) {
		return getModulesManager().getFolderFullPathOfObject(gp);
	}

}
