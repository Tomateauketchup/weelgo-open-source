package com.weelgo.core.clock;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {

	public static boolean isTimeDef(long time) {
		return !isTimeNotDef(time);
	}

	public static boolean isTimeNotDef(long time) {
		if (time < 0)
			return true;
		return false;
	}

	public static String formatToHoursAndMinutes(long utc0, ZoneId zone) {
		return format(utc0, "HH:mm", zone);
	}

	public static String formatHours(long utc0, ZoneId zone) {
		return format(utc0, "HH", zone);
	}

	public static String formatMinutes(long utc0, ZoneId zone) {
		return format(utc0, "mm", zone);
	}

	public static String formatTime(long utc0, ZoneId zone) {
		return format(utc0, "HH:mm", zone);
	}

	public static String format(long utc0, String formatterString, ZoneId zone) {
		if (isTimeDef(utc0)) {

			ZonedDateTime t = ZonedDateTime.ofInstant(Instant.ofEpochMilli(utc0), zone);
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatterString);
			return t.format(formatter);

		}
		return "";
	}

}
