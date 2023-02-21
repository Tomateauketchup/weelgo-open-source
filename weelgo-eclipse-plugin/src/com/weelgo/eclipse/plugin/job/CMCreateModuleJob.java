package com.weelgo.eclipse.plugin.job;

import com.weelgo.core.IProgressMonitor;
import com.weelgo.eclipse.plugin.CMEvents;
import com.weelgo.eclipse.plugin.Factory;
import com.weelgo.eclipse.plugin.ImagesFactory;

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
	public boolean isUndoRedoAllModulesJob() {
		return false;
	}
	
	@Override
	public boolean isUndoRedoJob() {		
		return true;
	}

	@Override
	public void doRun(IProgressMonitor monitor) {
		String moduleId = getServices().createModule(monitor, folderContainer, moduleName, modulePackage);
		setModuleUniqueIdentifier(moduleId);
		sentEvent(CMEvents.MODULE_CREATED, moduleId);
	}
	
	@Override
	public String getUndoRedoLabel() {
		return "Create module";
	}

	@Override
	public String getUndoRedoIcon() {
		return ImagesFactory.CHAIN_MAPPING_ICON;
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
