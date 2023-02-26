package com.weelgo.core;

import static com.weelgo.core.CoreUtils.cleanString;

import java.util.ArrayList;

import com.weelgo.core.exceptions.ExceptionsUtils;
import com.weelgo.core.exceptions.WeelgoException;

public class ValidatorUtils {

	public static String[] invalidFileNameStrings = new String[] { "NUL", "*", "?", "<", ">", "|", "\"", "\\", "/", ":",
			".", "..", "#", "$", "+", "%", "!", "`", "&", "'", "{", "}", "=", "@" };

	public static String[] invalidPackageNameStrings;

	static {
		ArrayList<String> arl = new ArrayList<>();
		CoreUtils.putArrayIntoList(invalidFileNameStrings, arl);
		arl.add(" ");
		invalidPackageNameStrings = arl.toArray(new String[arl.size()]);
	}

	public static void checkTaskName(String name) {
		if (!isValidTaskName(name)) {
			ExceptionsUtils.throwDynamicException(WeelgoException.INVALID_TASK_NAME);
		}
	}

	public static void checkGroupName(String name) {
		if (!isValidGroupName(name)) {
			ExceptionsUtils.throwDynamicException(WeelgoException.INVALID_GROUP_NAME);
		}
	}

	public static void checkPackageName(String name) {
		if (!isValidPackageName(name)) {
			ExceptionsUtils.throwDynamicException(WeelgoException.INVALID_PACKAGE_NAME);
		}
	}

	public static boolean isValidPackageName(String name) {
		return isValidString(name, Constants.NAME_MAX_LENGTH, false, invalidPackageNameStrings);
	}

	public static boolean isValidModuleName(String name) {
		return isValidFileName(name);
	}

	public static boolean isValidGroupName(String name) {
		return isValidFileName(name);
	}

	public static boolean isValidTaskName(String name) {
		return isValidFileName(name);
	}

	public static boolean isValidFileName(String name) {
		return isValidString(name, Constants.NAME_MAX_LENGTH, false, invalidFileNameStrings);
	}

	public static boolean isValidString(String name, int maxLength, boolean allowEmpty, String[] invalidStrings) {
		name = cleanString(name);
		if (!allowEmpty) {
			if (!CoreUtils.isNotNullOrEmpty(name)) {
				return false;
			}
		}
		if (name.length() > maxLength) {
			return false;
		}
		for (String str : invalidStrings) {
			if (name.contains(str)) {
				return false;
			}
		}

		return true;

	}

}
