package com.weelgo.core.json.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import com.weelgo.core.CoreFactory;
import com.weelgo.core.CoreUtils;
import com.weelgo.core.clock.ClockServices;
import com.weelgo.core.clock.IClockServices;
import com.weelgo.core.exceptions.WeelgoDynamicException;
import com.weelgo.core.exceptions.WeelgoException;

public class GenericTest implements IClockServices {

	@Rule
	public TemporaryFolder tmpFolder = new TemporaryFolder();

	public IClockServices clockServices = CoreFactory.create(ClockServices.class);

	public File createFolder() throws IOException {
		return tmpFolder.newFolder();
	}

	public void assertNotNullOrEmpty(Object o) {
		try {
			assertNotNull(o);
			CoreUtils.assertNotNullOrEmpty(o);
		} catch (Exception e) {
			fail("Object null or empty");
		}

	}

	public void assertNullOrEmpty(Object o) {
		try {
			CoreUtils.assertNotNullOrEmpty(o);
			fail("Object is not null or empty");
		} catch (Exception e) {

		}

	}

	public void assertDynamicException(String expectedType, Exception e) {
		assertTrue("Exception null", e != null);
		assertTrue("Exception not of type WeelgoDynamicException : " + e.getClass(),
				e instanceof WeelgoDynamicException);
		assertEquals(expectedType, ((WeelgoException) e).getType());
	}

	public static String generateUTF8String(String str) {
		return str + "티아라漢字 ط ظ ع";
	}

	public void assertDirectoryAndExist(File f) {
		assertTrue(f.exists() && f.isDirectory());
	}

	public void assertNotExist(File f) {
		assertFalse(f.exists());
	}

	public void assertListEquals(List l1, List l2) {
		if (l1 == null && l2 == null) {
			return;
		}
		assertTrue(l1 != null && l2 != null);
		assertTrue(l1.size() == l2.size());
		for (int i = 0; i < l1.size(); i++) {
			Object o1 = l1.get(i);
			Object o2 = l2.get(i);
			assertTrue(o1.equals(o2));
		}
	}

	@Override
	public long getUTC_O() {
		return clockServices.getUTC_O();
	}
}
