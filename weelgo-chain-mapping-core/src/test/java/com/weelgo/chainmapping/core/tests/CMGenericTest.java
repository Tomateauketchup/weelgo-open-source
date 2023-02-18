package com.weelgo.chainmapping.core.tests;

import java.io.File;

import com.weelgo.chainmapping.core.CMFileSystemDataSource;
import com.weelgo.chainmapping.core.CMGroup;
import com.weelgo.chainmapping.core.CMModuleService;
import com.weelgo.chainmapping.core.CMModulesManager;
import com.weelgo.core.json.tests.GenericTest;

public class CMGenericTest extends GenericTest {

	public CMModulesManager createModulesManager(CMModulesManager mm) throws Exception {
		return createModulesManager((File) getFileSystemDataSource(mm).getRootFolder());
	}

	public CMModulesManager createModulesManager() throws Exception {
		return createModulesManager(createFolder());
	}

	public CMModulesManager createModulesManager(File rootFolder) throws Exception {
		CMFileSystemDataSource ds = new CMFileSystemDataSource();
		ds.setRootFolder(rootFolder);
		CMModulesManager modManager = new CMModulesManager();
		modManager.getSources().add(ds);

		return modManager;
	}

	public String createModule(CMModulesManager mm, String name, String packageName) throws Exception {
		CMFileSystemDataSource ds = getFileSystemDataSource(mm);
		return createModule(mm, ds.getRootFolder(), name, packageName);
	}

	public String createModule(CMModulesManager mm, Object containerFolderOfCMGroup, String name, String packageName)
			throws Exception {
		CMFileSystemDataSource ds = getFileSystemDataSource(mm);
		String id = mm.createModule(null, ds, containerFolderOfCMGroup, name, packageName);
		mm.getServiceByModuleUniqueIdentifierId(id).getUndoRedoManager().saveModel();
		return id;
	}

	public CMFileSystemDataSource getFileSystemDataSource(CMModulesManager mm) {
		return (CMFileSystemDataSource) mm.getSources().get(0);
	}

	public CMGroup createGroup(CMModulesManager mm, CMModuleService modServ, String name, String packageName,
			CMGroup parenGp) {
		CMGroup gp = modServ.createGroup(mm, name, packageName, parenGp.getUuid());
		saveInUndoRedoManager(modServ);
		return gp;

	}

	public void loadAllModules(CMModulesManager mm) {
		mm.loadAllModules(null);
		mm.saveAllModelsForUndoRedo();
		mm.markAllModelAsNotDirty();
	}

	public CMGroup createGroup(CMModulesManager mm, CMModuleService modServ, String packageName, CMGroup parenGp) {
		return createGroup(mm, modServ, packageName, packageName, parenGp);
	}

	public void saveInUndoRedoManager(CMModuleService modServ) {
		if (modServ != null && modServ.getUndoRedoManager() != null) {
			modServ.getUndoRedoManager().saveModel();
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
