package com.weelgo.core.tests;

import static org.junit.Assert.*;

import java.time.ZoneId;

import org.junit.Test;

import com.weelgo.core.CoreUtils;
import com.weelgo.core.ValidatorUtils;
import com.weelgo.core.clock.DateUtils;
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

	@Test
	public void testClockUtils() {

		long now = getUTC_O();
		ZoneId zone = ZoneId.of("UTC+00:00");
		int hours = Integer.valueOf(DateUtils.formatHours(now, zone));
		int minutes = Integer.valueOf(DateUtils.formatMinutes(now, zone));		

		zone = ZoneId.of("UTC+01:00");
		int hours2 = Integer.valueOf(DateUtils.formatHours(now, zone));
		int minutes2 = Integer.valueOf(DateUtils.formatMinutes(now, zone));

		assertEquals(hours == 23 ? 0 : hours + 1, hours2);
		assertEquals(minutes, minutes2);

	}
}
