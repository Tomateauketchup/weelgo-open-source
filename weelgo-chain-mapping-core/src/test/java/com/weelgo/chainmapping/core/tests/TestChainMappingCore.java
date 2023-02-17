package com.weelgo.chainmapping.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Test;

import com.weelgo.chainmapping.core.CMFileSystemDataSource;
import com.weelgo.chainmapping.core.CMGroup;
import com.weelgo.chainmapping.core.CMModuleService;
import com.weelgo.chainmapping.core.CMModulesManager;
import com.weelgo.core.exceptions.WeelgoException;

public class TestChainMappingCore extends CMGenericTest {

	@Test
	public void test() throws Exception {

		CMModulesManager modManager = createModulesManager();
		String id = createModule(modManager, "Mon module", "mon_module");
		CMFileSystemDataSource ds = getFileSystemDataSource(modManager);
		assertFalse(modManager.isModulePackageFree(null, ds, ds.getRootFolder(), "mon_module"));

		try {
			modManager.createModule(null, ds, ds.getRootFolder(), "Mon module", "mon_module");
			fail();
		} catch (Exception e) {
			assertDynamicException(WeelgoException.MODULE_ALREADY_EXIST, e);
		}

	}

	@Test
	public void testUTF8() throws Exception {

		CMModulesManager modManager = createModulesManager();

		try {
			createModule(modManager, generateUTF8String("Mon module"), generateUTF8String("mon_module"));
			fail();
		} catch (Exception e) {
			assertDynamicException(WeelgoException.INVALID_PACKAGE_NAME, e);
		}

		String modId1 = createModule(modManager, generateUTF8String("Mon module"),
				generateUTF8String("mon_module").replaceAll(" ", ""));

		CMModuleService modServ1 = modManager.getServiceByModuleUniqueIdentifierId(modId1);
		assertEquals(generateUTF8String("Mon module"), modServ1.getName());
		assertEquals(generateUTF8String("Mon module"), modServ1.getRootGroup().getName());
		assertEquals(generateUTF8String("mon_module").replaceAll(" ", ""), modServ1.getRootGroup().getPackageName());
	}

	@Test
	public void testFindService() throws Exception {

		CMModulesManager modManager = createModulesManager();
		String modId1 = createModule(modManager, "Mon module", "mon_module");
		String modId2 = createModule(modManager, "Mon module 2", "mon_module_2");

		CMModuleService modServ1 = modManager.getServiceByModuleUniqueIdentifierId(modId1);
		CMModuleService modServ2 = modManager.getServiceByModuleUniqueIdentifierId(modId2);

		CMGroup gp1_1 = createGroup(modServ1, "mon_gp1", modServ1.getRootGroup());
		CMGroup gp2_1 = createGroup(modServ1, "mon_gp2", gp1_1);

		CMGroup gp1_2 = createGroup(modServ2, "mon_gp1", modServ2.getRootGroup());
		CMGroup gp2_2 = createGroup(modServ2, "mon_gp2", gp1_2);

		modManager.saveAll(null);

		File serv1RootFolder = (File) modServ1.getParentContainer();
		File serv2RootFolder = (File) modServ2.getParentContainer();

		File gp1_1Folder = new File(serv1RootFolder, "mon_gp1");
		assertTrue(gp1_1Folder.exists());
		assertTrue(gp1_1Folder.isDirectory());
		File gp2_1Folder = new File(gp1_1Folder, "mon_gp2");
		assertTrue(gp2_1Folder.exists());
		assertTrue(gp2_1Folder.isDirectory());

		File gp1_2Folder = new File(serv2RootFolder, "mon_gp1");
		assertTrue(gp1_2Folder.exists());
		assertTrue(gp1_2Folder.isDirectory());
		File gp2_2Folder = new File(gp1_2Folder, "mon_gp2");
		assertTrue(gp2_2Folder.exists());
		assertTrue(gp2_2Folder.isDirectory());

		assertNullOrEmpty(modManager.findModuleUniqueIdentifierId(""));
		assertNullOrEmpty(modManager.findModuleUniqueIdentifierId(getFileSystemDataSource(modManager).getRootFolder()));

		String idTmp = modManager.findModuleUniqueIdentifierId(gp2_1Folder);
		assertEquals(modId1, idTmp);
		idTmp = modManager.findModuleUniqueIdentifierId(gp1_1Folder);
		assertEquals(modId1, idTmp);
		idTmp = modManager.findModuleUniqueIdentifierId(serv1RootFolder);
		assertEquals(modId1, idTmp);
		idTmp = modManager.findModuleUniqueIdentifierId(modServ1);
		assertEquals(modId1, idTmp);

		idTmp = modManager.findModuleUniqueIdentifierId(gp2_2Folder);
		assertEquals(modId2, idTmp);
		idTmp = modManager.findModuleUniqueIdentifierId(gp1_2Folder);
		assertEquals(modId2, idTmp);
		idTmp = modManager.findModuleUniqueIdentifierId(serv2RootFolder);
		assertEquals(modId2, idTmp);
		idTmp = modManager.findModuleUniqueIdentifierId(modServ2);
		assertEquals(modId2, idTmp);
	}

	@Test
	public void testUndoRedo() throws Exception {

		CMModulesManager modManager = createModulesManager();
		String modId1 = createModule(modManager, "Module 1", "module_1");
		CMModuleService modServ1 = modManager.getServiceByModuleUniqueIdentifierId(modId1);
		CMGroup rootGp = modServ1.getRootGroup();
		CMGroup gp1 = createGroup(modServ1, "gp1", rootGp);
		assertNotNullOrEmpty(modServ1.getObject(gp1));

		modServ1.getUndoRedoManager().undo();
		assertNullOrEmpty(modServ1.getObject(gp1));

		modServ1.getUndoRedoManager().redo();
		assertNotNullOrEmpty(modServ1.getObject(gp1));

		CMGroup gp2 = createGroup(modServ1, "gp2", gp1);
		assertNotNullOrEmpty(modServ1.getObject(gp2));

		modServ1.getUndoRedoManager().undo();
		modServ1.getUndoRedoManager().undo();

		assertNullOrEmpty(modServ1.getObject(gp1));
		assertNullOrEmpty(modServ1.getObject(gp2));

		modServ1.getUndoRedoManager().redo();

		assertNotNullOrEmpty(modServ1.getObject(gp1));
		assertNullOrEmpty(modServ1.getObject(gp2));

		modServ1.getUndoRedoManager().redo();

		assertNotNullOrEmpty(modServ1.getObject(gp1));
		assertNotNullOrEmpty(modServ1.getObject(gp2));
		
		gp1=modServ1.getObject(gp1);
		modServ1.getUndoRedoManager().undo();
		assertEquals(gp1, modServ1.getObject(gp1));
		modServ1.getUndoRedoManager().redo();
		assertEquals(gp1, modServ1.getObject(gp1));
		
	}
}
