package com.weelgo.core.json.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import com.weelgo.core.exceptions.WeelgoDynamicException;
import com.weelgo.core.exceptions.WeelgoException;

public class GenericTest {

	@Rule
	public TemporaryFolder tmpFolder = new TemporaryFolder();

	public File createFolder() throws IOException {
		return tmpFolder.newFolder();
	}

	public void assertDynamicException(String expectedType, Exception e) {
		assertTrue("Exception null", e != null);
		assertTrue("Exception not of type WeelgoDynamicException : " + e.getClass(),
				e instanceof WeelgoDynamicException);
		assertEquals(expectedType, ((WeelgoException) e).getType());
	}

}
