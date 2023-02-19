package com.weelgo.chainmapping.core.tests;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.List;

import org.junit.Test;

import com.weelgo.chainmapping.core.CMFileSystemDataSource;
import com.weelgo.chainmapping.core.CMGroup;
import com.weelgo.chainmapping.core.CMModuleService;
import com.weelgo.chainmapping.core.CMModulesManager;
import com.weelgo.core.CoreUtils;
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

		CMGroup gp1_1 = createGroup(modManager, modServ1, "mon_gp1", modServ1.getRootGroup());
		CMGroup gp2_1 = createGroup(modManager, modServ1, "mon_gp2", gp1_1);

		CMGroup gp1_2 = createGroup(modManager, modServ2, "mon_gp1", modServ2.getRootGroup());
		CMGroup gp2_2 = createGroup(modManager, modServ2, "mon_gp2", gp1_2);

		saveAllModules(modManager);

		File serv1RootFolder = (File) modServ1.getContainer();
		File serv2RootFolder = (File) modServ2.getContainer();

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
	public void testCheckCreateGroupName() throws Exception {

		CMModulesManager modManager = createModulesManager();
		String modId1 = createModule(modManager, "Module 1", "module_1");
		CMModuleService modServ1 = modManager.getServiceByModuleUniqueIdentifierId(modId1);
		CMGroup root1 = modServ1.getRootGroup();

		String modId2 = createModule(modManager,root1, "new_group_1", "new_group_1");
		CMModuleService modServ2 = modManager.getServiceByModuleUniqueIdentifierId(modId2);
		CMGroup root2 = modServ2.getRootGroup();

		String[] ret = modManager.findNameForNewGroup(modServ1, root1.getUuid());
		assertArrayEquals(new String[] { "New Group 2", "new_group_2" }, ret);

		CMGroup new_group_2 = createGroup(modManager, modServ1, "New_Group_2", "new_group_2", root1);
		
		ret = modManager.findNameForNewGroup(modServ1, new_group_2.getUuid());
		assertArrayEquals(new String[] { "New Group 1", "new_group_1" }, ret);

	}

	@Test
	public void testUndoRedo() throws Exception {

		CMModulesManager modManager = createModulesManager();
		String modId1 = createModule(modManager, "Module 1", "module_1");
		CMModuleService modServ1 = modManager.getServiceByModuleUniqueIdentifierId(modId1);
		assertFalse(modServ1.isDirty());
		CMGroup rootGp = modServ1.getRootGroup();
		CMGroup gp1 = createGroup(modManager, modServ1, "gp1", rootGp);
		assertNotNullOrEmpty(modServ1.getObject(gp1));
		assertTrue(modServ1.isDirty());
		assertNotNullOrEmpty(modServ1.getContainer());

		undo(modServ1);
		assertNullOrEmpty(modServ1.getObject(gp1));
		assertFalse(modServ1.isDirty());
		assertNotNullOrEmpty(modServ1.getContainer());

		redo(modServ1);
		assertNotNullOrEmpty(modServ1.getObject(gp1));
		assertTrue(modServ1.isDirty());

		CMGroup gp2 = createGroup(modManager, modServ1, "gp2", gp1);
		assertNotNullOrEmpty(modServ1.getObject(gp2));
		assertTrue(modServ1.isDirty());

		undo(modServ1);
		undo(modServ1);

		assertNullOrEmpty(modServ1.getObject(gp1));
		assertNullOrEmpty(modServ1.getObject(gp2));
		assertFalse(modServ1.isDirty());

		redo(modServ1);

		assertNotNullOrEmpty(modServ1.getObject(gp1));
		assertNullOrEmpty(modServ1.getObject(gp2));
		assertTrue(modServ1.isDirty());

		redo(modServ1);

		assertNotNullOrEmpty(modServ1.getObject(gp1));
		assertNotNullOrEmpty(modServ1.getObject(gp2));
		assertTrue(modServ1.isDirty());

		gp1 = modServ1.getObject(gp1);
		undo(modServ1);
		assertEquals(gp1, modServ1.getObject(gp1));
		assertTrue(modServ1.isDirty());

		redo(modServ1);
		assertEquals(gp1, modServ1.getObject(gp1));
		assertTrue(modServ1.isDirty());

		saveModule(modManager, modServ1);
		assertFalse(modServ1.isDirty());

		undo(modServ1);
		assertTrue(modServ1.isDirty());

		redo(modServ1);
		assertFalse(modServ1.isDirty());

		// This redo is useless
		redo(modServ1);
		assertFalse(modServ1.isDirty());

		undo(modServ1);
		assertTrue(modServ1.isDirty());
		saveModule(modManager, modServ1);
		assertFalse(modServ1.isDirty());

		redo(modServ1);
		assertTrue(modServ1.isDirty());

		saveModule(modManager, modServ1);
		assertFalse(modServ1.isDirty());

		CMModulesManager modManager2 = createModulesManager(modManager);
		loadAllModules(modManager2);
		CMModuleService modServ1_2 = modManager2.getServiceByModuleUniqueIdentifierId(modId1);
		assertFalse(modServ1_2.isDirty());

		createGroup(modManager2, modServ1_2, "gpTest", modServ1_2.getRootGroup());
		assertTrue(modServ1_2.isDirty());

		loadAllModules(modManager2);
		assertFalse(modServ1_2.isDirty());

	}

	@Test
	public void testCreateModuleInsideModule2() throws Exception {

		CMModulesManager modManager = createModulesManager();
		String modId1 = createModule(modManager, "Module 1", "module_1");
		CMModuleService modServ1 = modManager.getServiceByModuleUniqueIdentifierId(modId1);
		File rootFolder1 = (File) modServ1.getContainer();
		CMGroup rootGroup = modServ1.getRootGroup();

		CMGroup gp1_1 = createGroup(modManager, modServ1, "gp1_1", rootGroup);
		File gp1_1Folder = new File(rootFolder1, "gp1_1");
		assertFalse(gp1_1Folder.exists());

		CMGroup gp2_1 = createGroup(modManager, modServ1, "gp2_1", rootGroup);
		File gp2_1Folder = new File(rootFolder1, "gp2_1");
		assertNotExist(gp2_1Folder);

		String modId2 = createModule(modManager, gp1_1, "Module 2", "module_2");
		CMModuleService modServ2 = modManager.getServiceByModuleUniqueIdentifierId(modId2);
		File rootFolder2 = (File) modServ2.getContainer();
		CMGroup rootGroup2 = modServ2.getRootGroup();

		assertDirectoryAndExist(gp1_1Folder);
		assertNotExist(gp2_1Folder);
		assertDirectoryAndExist(rootFolder2);

		CMGroup gp1_2 = createGroup(modManager, modServ2, "gp1_2", rootGroup2);
		File gp1_2Folder = new File(rootFolder2, "gp1_2");

		assertDirectoryAndExist(gp1_1Folder);
		assertNotExist(gp2_1Folder);
		assertDirectoryAndExist(rootFolder2);
		assertNotExist(gp1_2Folder);

		String modId3 = createModule(modManager, rootGroup2, "Module 3", "module_3");
		CMModuleService modServ3 = modManager.getServiceByModuleUniqueIdentifierId(modId3);
		File rootFolder3 = (File) modServ3.getContainer();
		CMGroup rootGroup3 = modServ3.getRootGroup();

		assertDirectoryAndExist(rootFolder1);
		assertDirectoryAndExist(gp1_1Folder);
		assertNotExist(gp2_1Folder);
		assertDirectoryAndExist(rootFolder2);
		assertNotExist(gp1_2Folder);
		assertDirectoryAndExist(rootFolder3);

		List<Object> childs = modManager.getChildsForTreeNavigator(rootGroup);
		List lst = CoreUtils.putArrayIntoList(gp1_1, gp2_1);
		assertListEquals(childs, lst);

		childs = modManager.getChildsForTreeNavigator(gp1_1);
		lst = CoreUtils.putObjectIntoList(modServ2);
		assertListEquals(childs, lst);

		childs = modManager.getChildsForTreeNavigator(gp2_1);
		lst = CoreUtils.putObjectIntoList(null);
		assertListEquals(childs, lst);

		childs = modManager.getChildsForTreeNavigator(rootGroup2);
		lst = CoreUtils.putArrayIntoList(gp1_2, modServ3);
		assertListEquals(childs, lst);

		childs = modManager.getChildsForTreeNavigator(gp1_2);
		lst = CoreUtils.putObjectIntoList(null);
		assertListEquals(childs, lst);

		childs = modManager.getChildsForTreeNavigator(rootGroup3);
		lst = CoreUtils.putObjectIntoList(null);
		assertListEquals(childs, lst);

		Object parent = modManager.getParentForTreeNavigator(modServ1);
		assertTrue(parent instanceof File);

		parent = modManager.getParentForTreeNavigator(modServ2);
		assertEquals(gp1_1, parent);

		parent = modManager.getParentForTreeNavigator(modServ3);
		assertEquals(rootGroup2, parent);

		saveModule(modManager, modServ1);
		assertDirectoryAndExist(gp1_1Folder);
		assertDirectoryAndExist(gp2_1Folder);
		assertDirectoryAndExist(rootFolder2);
		assertNotExist(gp1_2Folder);

		saveModule(modManager, modServ2);
		assertDirectoryAndExist(rootFolder1);
		assertDirectoryAndExist(rootFolder2);
		assertDirectoryAndExist(rootFolder3);
		assertDirectoryAndExist(gp1_1Folder);
		assertDirectoryAndExist(gp2_1Folder);
		assertDirectoryAndExist(rootFolder2);
		assertDirectoryAndExist(gp1_2Folder);

		CMGroup gp_mod3_1 = createGroup(modManager, modServ3, "gp_mod3_1", rootGroup3);
		File gp_mod3_1Folder = new File(rootFolder3, "gp_mod3_1");
		CMGroup gp_mod3_2 = createGroup(modManager, modServ3, "gp_mod3_2", gp_mod3_1);
		File gp_mod3_2Folder = new File(gp_mod3_1Folder, "gp_mod3_2");
		CMGroup gp_mod3_3 = createGroup(modManager, modServ3, "gp_mod3_3", gp_mod3_2);
		File gp_mod3_3Folder = new File(gp_mod3_2Folder, "gp_mod3_3");

		saveAllModules(modManager);

		assertDirectoryAndExist(rootFolder1);
		assertDirectoryAndExist(rootFolder2);
		assertDirectoryAndExist(rootFolder3);
		assertDirectoryAndExist(gp1_1Folder);
		assertDirectoryAndExist(gp2_1Folder);
		assertDirectoryAndExist(rootFolder2);
		assertDirectoryAndExist(gp1_2Folder);
		assertDirectoryAndExist(gp_mod3_1Folder);
		assertDirectoryAndExist(gp_mod3_2Folder);
		assertDirectoryAndExist(gp_mod3_3Folder);

	}

	@Test
	public void testCreateModuleInsideModule() throws Exception {

		CMModulesManager modManager = createModulesManager();
		String modId1 = createModule(modManager, "Module 1", "module_1");
		CMModuleService modServ1 = modManager.getServiceByModuleUniqueIdentifierId(modId1);
		File rootFolder = (File) modServ1.getContainer();

		CMGroup rootGroup = modServ1.getRootGroup();
		CMGroup gp1 = createGroup(modManager, modServ1, "gp1", rootGroup);
		File gp1Folder = new File(rootFolder, "gp1");
		assertFalse(gp1Folder.exists());// The folder doesn't exist because not saved

		assertFalse(modManager.isModulePackageFree(null, getFileSystemDataSource(modManager), rootGroup, "gp1"));

		// We try to create module with same name
		try {
			createModule(modManager, rootGroup, "gp1", "gp1");
			fail();
		} catch (Exception e) {
			assertDynamicException(WeelgoException.GROUP_ALREADY_EXIST, e);
		}

		assertTrue(modManager.isModulePackageFree(null, getFileSystemDataSource(modManager), rootGroup, "module_2"));

		String modId2 = createModule(modManager, rootGroup, "Module 2", "module_2");
		CMModuleService modServ2 = modManager.getServiceByModuleUniqueIdentifierId(modId2);
		assertNotNullOrEmpty(modServ2);

		File mod2Folder = new File(rootFolder, "module_2");
		assertTrue(mod2Folder.exists() && mod2Folder.isDirectory());

		try {
			createGroup(modManager, modServ1, "module_2", rootGroup);
			fail();
		} catch (Exception e) {
			assertDynamicException(WeelgoException.MODULE_ALREADY_EXIST, e);
		}

	}

	@Test
	public void testSave2() throws Exception {

		CMModulesManager modManager = createModulesManager();
		String modId1 = createModule(modManager, "Module 1", "module_1");
		CMModuleService modServ1 = modManager.getServiceByModuleUniqueIdentifierId(modId1);
		CMGroup rootGp = modServ1.getRootGroup();
		File rootFolder = (File) modServ1.getContainer();

		CMGroup gp1 = createGroup(modManager, modServ1, "gp1", rootGp);
		CMGroup gp2 = createGroup(modManager, modServ1, "gp2", gp1);
		CMGroup gp3 = createGroup(modManager, modServ1, "gp3", gp2);

		saveAllModules(modManager);

		File gp1Folder = new File(rootFolder, "gp1");
		File gp2Folder = new File(gp1Folder, "gp2");
		File gp3Folder = new File(gp2Folder, "gp3");
		assertTrue(gp1Folder.exists() && gp1Folder.isDirectory());
		assertTrue(gp2Folder.exists() && gp2Folder.isDirectory());
		assertTrue(gp3Folder.exists() && gp3Folder.isDirectory());

		undo(modServ1);
		undo(modServ1);

		// We undo but we don't save. The folder still exist. Now we will create group
		// with exactly same for one
		assertTrue(gp1Folder.exists() && gp1Folder.isDirectory());
		assertTrue(gp2Folder.exists() && gp2Folder.isDirectory());
		assertTrue(gp3Folder.exists() && gp3Folder.isDirectory());

		assertNotNullOrEmpty(modServ1.getObject(gp1));
		assertNullOrEmpty(modServ1.getObject(gp2));
		assertNullOrEmpty(modServ1.getObject(gp3));

		gp2 = createGroup(modManager, modServ1, "gp2", gp1);
		CMGroup gp4 = createGroup(modManager, modServ1, "gp4", gp2);

		assertTrue(gp1Folder.exists() && gp1Folder.isDirectory());
		assertTrue(gp2Folder.exists() && gp2Folder.isDirectory());
		assertTrue(gp3Folder.exists() && gp3Folder.isDirectory());

		// Now we save
		saveAllModules(modManager);

		File gp4Folder = new File(gp2Folder, "gp4");

		// Normally the folder gp3 is removed abd there is a folder gp4
		assertTrue(gp1Folder.exists() && gp1Folder.isDirectory());
		assertTrue(gp2Folder.exists() && gp2Folder.isDirectory());
		assertFalse(gp3Folder.exists());
		assertTrue(gp4Folder.exists() && gp4Folder.isDirectory());

	}

	@Test
	public void testSave3() throws Exception {
		CMModulesManager modManager = createModulesManager();
		String modId1 = createModule(modManager, "Module 1", "module_1");
		CMModuleService modServ1 = modManager.getServiceByModuleUniqueIdentifierId(modId1);
		CMGroup rootGp = modServ1.getRootGroup();
		File rootFolder = (File) modServ1.getContainer();

		CMGroup gp1 = createGroup(modManager, modServ1, "gp1", rootGp);
		CMGroup gp2 = createGroup(modManager, modServ1, "gp2", gp1);
		CMGroup gp3 = createGroup(modManager, modServ1, "gp3", gp2);

		saveAllModules(modManager);

		File gp1Folder = new File(rootFolder, "gp1");
		File gp2Folder = new File(gp1Folder, "gp2");
		File gp3Folder = new File(gp2Folder, "gp3");
		assertTrue(gp1Folder.exists() && gp1Folder.isDirectory());
		assertTrue(gp2Folder.exists() && gp2Folder.isDirectory());
		assertTrue(gp3Folder.exists() && gp3Folder.isDirectory());

		undo(modServ1);
		undo(modServ1);

		// We undo but we don't save. The folder still exist. Now we will create group
		// with exactly same for one
		assertTrue(gp1Folder.exists() && gp1Folder.isDirectory());
		assertTrue(gp2Folder.exists() && gp2Folder.isDirectory());
		assertTrue(gp3Folder.exists() && gp3Folder.isDirectory());

		assertNotNullOrEmpty(modServ1.getObject(gp1));
		assertNullOrEmpty(modServ1.getObject(gp2));
		assertNullOrEmpty(modServ1.getObject(gp3));

		// Now we will create a module with the same name as gp2 that s removed from
		// model but not from filesystem
		String gp2Module = createModule(modManager, gp1, "gp2", "gp2");

		// Normally the module is save so it removed folder 3
		assertTrue(gp1Folder.exists() && gp1Folder.isDirectory());
		assertTrue(gp2Folder.exists() && gp2Folder.isDirectory());
		assertFalse(gp3Folder.exists());

	}

	@Test
	public void testSave() throws Exception {

		CMModulesManager modManager = createModulesManager();
		String modId1 = createModule(modManager, "Module 1", "module_1");
		CMModuleService modServ1 = modManager.getServiceByModuleUniqueIdentifierId(modId1);
		assertFalse(modServ1.isDirty());
		File rootFolder = (File) modServ1.getContainer();
		CMGroup rootGp = modServ1.getRootGroup();
		CMGroup gp1 = createGroup(modManager, modServ1, "gp1", rootGp);
		CMGroup gp2 = createGroup(modManager, modServ1, "gp2", gp1);
		CMGroup gp3 = createGroup(modManager, modServ1, "gp3", gp1);
		CMGroup gp4 = createGroup(modManager, modServ1, "gp4", gp3);

		File gp1Folder = new File(rootFolder, "gp1");
		File gp2Folder = new File(gp1Folder, "gp2");
		File gp3Folder = new File(gp1Folder, "gp3");
		File gp4Folder = new File(gp3Folder, "gp4");

		assertTrue(rootFolder.exists() && rootFolder.isDirectory());
		assertFalse(gp1Folder.exists());
		assertFalse(gp2Folder.exists());
		assertFalse(gp3Folder.exists());
		assertFalse(gp4Folder.exists());

		saveModule(modManager, modServ1);

		assertTrue(rootFolder.exists() && rootFolder.isDirectory());
		assertTrue(gp1Folder.exists() && gp1Folder.isDirectory());
		assertTrue(gp2Folder.exists() && gp2Folder.isDirectory());
		assertTrue(gp3Folder.exists() && gp3Folder.isDirectory());
		assertTrue(gp4Folder.exists() && gp4Folder.isDirectory());

		undo(modServ1);
		saveModule(modManager, modServ1);

		assertTrue(rootFolder.exists() && rootFolder.isDirectory());
		assertTrue(gp1Folder.exists() && gp1Folder.isDirectory());
		assertTrue(gp2Folder.exists() && gp2Folder.isDirectory());
		assertTrue(gp3Folder.exists() && gp3Folder.isDirectory());
		assertFalse(gp4Folder.exists());

		undo(modServ1);
		saveModule(modManager, modServ1);

		assertTrue(rootFolder.exists() && rootFolder.isDirectory());
		assertTrue(gp1Folder.exists() && gp1Folder.isDirectory());
		assertTrue(gp2Folder.exists() && gp2Folder.isDirectory());
		assertFalse(gp3Folder.exists());
		assertFalse(gp4Folder.exists());

		redo(modServ1);
		redo(modServ1);
		saveModule(modManager, modServ1);

		assertTrue(rootFolder.exists() && rootFolder.isDirectory());
		assertTrue(gp1Folder.exists() && gp1Folder.isDirectory());
		assertTrue(gp2Folder.exists() && gp2Folder.isDirectory());
		assertTrue(gp3Folder.exists() && gp3Folder.isDirectory());
		assertTrue(gp4Folder.exists() && gp4Folder.isDirectory());

		undo(modServ1);
		undo(modServ1);
		undo(modServ1);
		saveModule(modManager, modServ1);
		assertTrue(rootFolder.exists() && rootFolder.isDirectory());
		assertTrue(gp1Folder.exists() && gp1Folder.isDirectory());
		assertFalse(gp2Folder.exists());
		assertFalse(gp3Folder.exists());
		assertFalse(gp4Folder.exists());

	}
}
