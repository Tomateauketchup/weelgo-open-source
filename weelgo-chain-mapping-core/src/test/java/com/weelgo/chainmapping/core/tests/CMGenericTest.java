package com.weelgo.chainmapping.core.tests;

import java.io.File;

import com.weelgo.chainmapping.core.CMFileSystemDataSource;
import com.weelgo.chainmapping.core.CMGroup;
import com.weelgo.chainmapping.core.CMModuleService;
import com.weelgo.chainmapping.core.CMModulesManager;
import com.weelgo.chainmapping.core.CMNode;
import com.weelgo.chainmapping.core.CMTask;
import com.weelgo.core.CoreUtils;
import com.weelgo.core.IUuidObject;
import com.weelgo.core.json.tests.GenericTest;

public class CMGenericTest extends GenericTest {

	public static final String FILESYSTEM_DATASOURCE_UUID = "filesystem-data-source";

	public CMModulesManager createModulesManager(CMModulesManager mm) throws Exception {
		return createModulesManager((File) getFileSystemDataSource(mm).getRootFolder());
	}

	public CMModulesManager createModulesManager() throws Exception {
		return createModulesManager(createFolder());
	}

	public CMModulesManager createModulesManager(File rootFolder) throws Exception {
		CMFileSystemDataSource ds = new CMFileSystemDataSource();
		ds.setUuid(FILESYSTEM_DATASOURCE_UUID);
		ds.setRootFolder(rootFolder);
		CMModulesManager modManager = new CMModulesManager();
		modManager.addDataSource(ds);

		return modManager;
	}

	public String createModule(CMModulesManager mm, String name, String packageName) throws Exception {
		CMFileSystemDataSource ds = getFileSystemDataSource(mm);
		return createModule(mm, ds.getRootFolder(), name, packageName);
	}

	public String createModule(CMModulesManager mm, Object containerFolderOfCMGroup, String name, String packageName)
			throws Exception {
		CMFileSystemDataSource ds = getFileSystemDataSource(mm);
		String id = mm.createModule(null, containerFolderOfCMGroup, name, packageName, ds.getUuid());
		mm.getServiceByModuleUniqueIdentifierId(id).getUndoRedoManager().saveModel(null);
		return id;
	}

	public CMFileSystemDataSource getFileSystemDataSource(CMModulesManager mm) {
		return (CMFileSystemDataSource) mm.getDataSourceByUuid(FILESYSTEM_DATASOURCE_UUID);
	}

	public CMGroup createGroup(CMModulesManager mm, CMModuleService modServ, String name, String packageName,
			CMGroup parenGp) {
		CMGroup gp = modServ.createGroup(mm, name, packageName, parenGp.getUuid());
		saveInUndoRedoManager(modServ);
		return gp;

	}

	public void loadAllModules(CMModulesManager mm) {
		mm.loadAllModules(null);
		mm.saveAllModelsForUndoRedo(null);
		mm.markAllModelAsNotDirty();
	}

	public CMGroup createGroup(CMModulesManager mm, CMModuleService modServ, String packageName, CMGroup parenGp) {
		return createGroup(mm, modServ, packageName, packageName, parenGp);
	}

	public CMTask createTask(CMModuleService modServ, String taskName, CMGroup parenGp) {
		CMTask t = modServ.createTask(taskName, parenGp.getUuid(), 0, 0);
		saveInUndoRedoManager(modServ);
		return t;
	}

	public void linkNodes(CMModuleService modServ, CMNode source, CMNode target) {
		modServ.linkNodes(source.getUuid(), target.getUuid());
		saveInUndoRedoManager(modServ);
	}

	public void moveElementsIntoGroup(CMModuleService modServ, CMGroup parentGroup, IUuidObject... object) {
		modServ.moveElementsIntoGroup(parentGroup.getUuid(), CoreUtils.transformArrayToStringArray(object));
		saveInUndoRedoManager(modServ);
	}

	public CMTask modifyTaskName(CMModuleService modServ, CMTask t, String newName) {
		t = modServ.modifyTaskName(newName, t.getUuid());
		saveInUndoRedoManager(modServ);
		return t;
	}

	public void saveInUndoRedoManager(CMModuleService modServ) {
		if (modServ != null && modServ.getUndoRedoManager() != null) {
			modServ.getUndoRedoManager().saveModel(null);
		}
	}

	public void saveAllModules(CMModulesManager mm) {
		mm.saveAll(null);
	}

	public void saveModule(CMModulesManager mm, CMModuleService ser) {
		mm.saveModule(null, ser.getModuleUniqueIdentifier());
	}

	public void undo(CMModuleService s) {
		s.getUndoRedoManager().undo();
	}

	public void redo(CMModuleService s) {
		s.getUndoRedoManager().redo();
	}
}
