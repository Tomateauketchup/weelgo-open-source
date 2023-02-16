package com.weelgo.chainmapping.core.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.weelgo.chainmapping.core.CMFileSystemDataSource;
import com.weelgo.chainmapping.core.CMModulesManager;
import com.weelgo.core.exceptions.WeelgoException;
import com.weelgo.core.json.tests.GenericTest;

public class TestChainMappingCore extends GenericTest {

	@Test
	public void test() throws Exception {

		CMFileSystemDataSource ds = new CMFileSystemDataSource();
		ds.setRootFolder(createFolder());
		CMModulesManager modManager = new CMModulesManager();
		modManager.getSources().add(ds);
		modManager.createModule(null, ds, ds.getRootFolder(), "Mon module", "mon_module");

		assertFalse(modManager.isModulePackageFree(null, ds, ds.getRootFolder(), "mon_module"));

		try {
			modManager.createModule(null, ds, ds.getRootFolder(), "Mon module", "mon_module");
			fail();
		} catch (Exception e) {
			assertDynamicException(WeelgoException.MODULE_ALREADY_EXIST, e);
		}

	}

}
