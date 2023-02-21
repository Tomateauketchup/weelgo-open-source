package com.weelgo.core.clock;

import java.time.Instant;

public class ClockServices implements IClockServices {

	@Override
	public long getUTC_O() {
		Instant now = Instant.now();
		return now.toEpochMilli();
	}

}
