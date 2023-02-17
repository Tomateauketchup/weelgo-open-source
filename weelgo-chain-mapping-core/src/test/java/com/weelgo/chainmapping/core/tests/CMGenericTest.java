package com.weelgo.chainmapping.core.tests;

import com.weelgo.chainmapping.core.CMFileSystemDataSource;
import com.weelgo.chainmapping.core.CMGroup;
import com.weelgo.chainmapping.core.CMModuleService;
import com.weelgo.chainmapping.core.CMModulesManager;
import com.weelgo.core.json.tests.GenericTest;

public class CMGenericTest extends GenericTest {

	public CMModulesManager createModulesManager() throws Exception {
		CMFileSystemDataSource ds = new CMFileSystemDataSource();
		ds.setRootFolder(createFolder());
		CMModulesManager modManager = new CMModulesManager();
		modManager.getSources().add(ds);

		return modManager;
	}

	public String createModule(CMModulesManager mm, String name, String packageName) throws Exception {
		CMFileSystemDataSource ds = getFileSystemDataSource(mm);
		String id = mm.createModule(null, ds, ds.getRootFolder(), name, packageName);
		mm.load(null);
		mm.getServiceByModuleUniqueIdentifierId(id).getUndoRedoManager().saveModel();
		return id;
	}

	public CMFileSystemDataSource getFileSystemDataSource(CMModulesManager mm) {
		return (CMFileSystemDataSource) mm.getSources().get(0);
	}

	public CMGroup createGroup(CMModuleService modServ, String name, String packageName, CMGroup parenGp) {
		CMGroup gp = modServ.createGroup(name, packageName, parenGp.getUuid());
		saveInUndoRedoManager(modServ);
		return gp;

	}

	public CMGroup createGroup(CMModuleService modServ, String packageName, CMGroup parenGp) {
		return createGroup(modServ, packageName, packageName, parenGp);
	}

	public void saveInUndoRedoManager(CMModuleService modServ) {
		if (modServ != null && modServ.getUndoRedoManager() != null) {
			modServ.getUndoRedoManager().saveModel();
		}
	}
}
