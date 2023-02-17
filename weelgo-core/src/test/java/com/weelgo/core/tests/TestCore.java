package com.weelgo.core.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import com.weelgo.core.CoreUtils;
import com.weelgo.core.ValidatorUtils;
import com.weelgo.core.json.tests.GenericTest;

public class TestCore extends GenericTest {

	@Test
	public void testCoreUtils() {

		String[] a = CoreUtils.getPackages("spok.oror");
		assertArrayEquals(new String[] { "spok", "oror" }, a);

		a = CoreUtils.getPackages("spok@oror dzeferf é@à ;efr");
		assertArrayEquals(new String[] { "spok@oror dzeferf é@à ;efr" }, a);

		a = CoreUtils.getPackages("spok@oror dzeferf é.@à ;efr");
		assertArrayEquals(new String[] { "spok@oror dzeferf é", "@à ;efr" }, a);

		assertTrue(ValidatorUtils.isValidPackageName("refetgtg_gtgfer_erfgt"));
		assertFalse(ValidatorUtils.isValidPackageName("refetgtg_gtg.fer_erfgt"));
		assertFalse(ValidatorUtils.isValidPackageName("refetgtg gtgfer_erfgt"));
		assertFalse(ValidatorUtils.isValidPackageName("refetgtg@gtgfer_erfgt"));

	}

}
