package com.weelgo.eclipse.plugin.job;

import com.weelgo.core.IProgressMonitor;
import com.weelgo.eclipse.plugin.Factory;

public class CMCreateModuleJob extends CMDatabaseModifierJob {

	private Object folderContainer;
	private String moduleName;
	private String modulePackage;

	public CMCreateModuleJob() {
		super("Create module", "Creating new module ...");
	}

	public static CMCreateModuleJob CREATE() {
		return Factory.create(CMCreateModuleJob.class);
	}

	@Override
	public void doRun(IProgressMonitor monitor) {
		getServices().createModule(monitor, folderContainer, moduleName, modulePackage);
	}

	public Object getFolderContainer() {
		return folderContainer;
	}

	public void setFolderContainer(Object folderContainer) {
		this.folderContainer = folderContainer;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getModulePackage() {
		return modulePackage;
	}

	public void setModulePackage(String modulePackage) {
		this.modulePackage = modulePackage;
	}

}
