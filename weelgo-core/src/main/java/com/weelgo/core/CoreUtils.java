package com.weelgo.core;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.weelgo.core.exceptions.AssertNotNullOrEmpty;
import com.weelgo.core.exceptions.AssertNotNullOrEmptyCustomFatal;
import com.weelgo.core.exceptions.ExceptionsUtils;

public class CoreUtils {

	public static boolean isAllNull(Object... a) {
		if (a != null) {
			for (Object object : a) {
				if (object != null) {
					return false;
				}
			}
		}
		return true;
	}

	public static boolean isStrictlyEqualsString(String o1, String o2) {
		if (o1 == null && o2 == null)
			return true;
		if (o1 != null && o2 != null)
			return o1.equals(o2);

		return false;
	}

	private static boolean isNullOrEmpty(String str) {
		if (str == null || str.isEmpty()) {
			return true;
		}
		return false;
	}

	public static boolean isNotNullOrEmpty(String str) {
		return !isNullOrEmpty(str);
	}

	public static String cleanString(String str) {
		if (str == null) {
			str = "";
		}
		return str.trim();
	}

	public static String generateUUIDString() {
		return generateUUID().toString();
	}

	public static UUID generateUUID() {
		UUID id = UUID.randomUUID();
		return id;
	}

	public static File getFileFromUrlPath(String urlPath) throws Exception {

		URL url = new URL(urlPath);
		File f = null;
		try {
			f = new File(url.toURI());
		} catch (URISyntaxException e) {
			f = new File(url.getPath());
		}
		return f;

	}

	public static String generateUUIDString(int nbChar) {
		char[] uuidChar = generateUUIDString().toCharArray();
		String newUUID = "";
		for (int i = 0; i < nbChar; i++) {
			newUUID += uuidChar[i];
		}

		return newUUID;
	}

	public static String removeEnd(final String str, final String elementToRemove) {
		if (isNullOrEmpty(str) || isNullOrEmpty(elementToRemove)) {
			return str;
		}
		if (str.endsWith(elementToRemove)) {
			return str.substring(0, str.length() - elementToRemove.length());
		}
		return str;
	}

	public static void assertFalse(boolean b) {
		assertTrue(!b);
	}

	public static void assertFalse(boolean b, String exceptionType) {
		assertTrue(!b, exceptionType);
	}

	public static void assertTrue(boolean b) {
		if (!b) {
			ExceptionsUtils.throwDynamicExceptionInvalidInput();
		}
	}

	public static void assertTrue(boolean b, String exceptionType) {
		if (!b) {
			ExceptionsUtils.throwDynamicException(exceptionType);
		}
	}

	public static void assertNotNullOrEmpty(Object o) {
		new AssertNotNullOrEmpty(o, "Object can't be null or empty.");
	}

	public static void assertNotNullOrEmptyFatal(Object o) {
		new AssertNotNullOrEmptyCustomFatal(o, "Object can't be null or empty.");
	}

	public static void assertNotNullOrEmptyFatal(Object o, String errorMessage) {
		new AssertNotNullOrEmptyCustomFatal(o, errorMessage);
	}
//	public static <T extends IUuidObject> void putIntoMap(List<T> a, Map<String, T> map) {
//		if (a != null && map != null) {
//			for (T o : a) {
//				if (o != null)
//					map.put(o.getUuid(), o);
//			}
//		}
//	}

	public static List putIntoList(Object[] sourceList) {
		ArrayList rl = new ArrayList<>();
		putIntoList(sourceList, rl);
		return rl;
	}

	public static void putIntoList(Object[] sourceList, List recieverList) {
		if (recieverList != null && sourceList != null) {
			for (Object object : sourceList) {
				recieverList.add(object);
			}
		}
	}

	public static void putIntoList(List sourceList, List recieverList) {
		if (recieverList != null && sourceList != null) {
			for (Object object : sourceList) {
				recieverList.add(object);
			}
		}
	}

	public static void putIntoList(Map<Object, Object> map, List a) {
		if (a != null && map != null) {
			for (Map.Entry<Object, Object> entry : map.entrySet()) {
				Object val = entry.getValue();
				a.add(val);
			}
		}
	}

	public static void putIntoMap(List a, Map map) {
		if (a != null && map != null) {
			for (Object o : a) {
				if (o != null)
					map.put(((IUuidObject) o).getUuid(), o);
			}
		}
	}

	public static Map putIntoMap(List a) {
		HashMap map = new HashMap<>();
		if (a != null && map != null) {
			for (Object o : a) {
				if (o != null)
					map.put(((IUuidObject) o).getUuid(), o);
			}
		}
		return map;
	}

	public static void removeFromMap(List a, Map map) {
		if (a != null && map != null) {
			for (Object o : a) {
				if (o != null)
					map.remove(((IUuidObject) o).getUuid());
			}
		}
	}

	public static String[] getPackages(String packageString) {
		packageString = cleanString(packageString);
		return packageString.split("\\" + Constants.UUID_PACKAGE_SEPARATOR);
	}

	public static String createPackageUuid(String uniqueIdentifier, String packageString) {
		return cleanString(uniqueIdentifier) + Constants.UNIQUE_IDENTIFIER_SEPARATOR + cleanString(packageString);
	}

	public static String createPackageString(String... childUuid) {
		String str = null;
		if (childUuid != null) {
			for (String stringTmp : childUuid) {

				stringTmp = cleanString(stringTmp);
				if (isNotNullOrEmpty(stringTmp)) {
					if (str == null) {
						str = stringTmp;
					} else {
						str = str + Constants.UUID_PACKAGE_SEPARATOR + stringTmp;
					}
				}
			}
		}

		return str;
	}

}
