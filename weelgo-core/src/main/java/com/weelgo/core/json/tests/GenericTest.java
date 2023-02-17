package com.weelgo.core.json.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import com.weelgo.core.CoreUtils;
import com.weelgo.core.exceptions.WeelgoDynamicException;
import com.weelgo.core.exceptions.WeelgoException;

public class GenericTest {

	@Rule
	public TemporaryFolder tmpFolder = new TemporaryFolder();

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
}
