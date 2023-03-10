package com.weelgo.core;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.weelgo.core.exceptions.AssertNotNullOrEmpty;
import com.weelgo.core.exceptions.AssertNotNullOrEmptyCustomFatal;
import com.weelgo.core.exceptions.ExceptionsUtils;

public class CoreUtils {

	public static IUuidGenerator uuidGenerator = new UuidGenerator();

	public static void disposeMap(Map objects) {
		if (objects != null && objects.size() > 0) {
			for (Object o : objects.values()) {
				if (o != null && o instanceof IDisposableObject) {
					dispose(o);
				}
			}
		}
	}

	public static void disposeList(List objects) {
		if (objects != null) {
			for (Object o : objects) {
				if (o != null && o instanceof IDisposableObject) {
					dispose(o);
				}
			}
		}
	}

	public static void dispose(Object o) {
		if (o != null) {
			if (o instanceof IDisposableObject) {
				((IDisposableObject) o).disposeObject();
			} else if (o instanceof List) {
				disposeList((List) o);
			} else if (o instanceof Map) {
				disposeMap((Map) o);
			}
		}
	}

	public static boolean isInstanceOf(Object o, Class... possibleClasses) {
		if (possibleClasses != null) {
			for (Class cTmp : possibleClasses) {
				if (cTmp != null && cTmp.isInstance(o)) {
					return true;
				}
			}
		}
		return false;
	}

	public static String getLinkUUID(String source, String target) {
		return source + "_" + target;
	}

	public static <T extends IUpdatableUuidObject> void updateList(List<T> listToUpdate, List<T> newList) {
		updateList(listToUpdate, newList, true);
	}

	private static <T extends IUpdatableUuidObject> void updateList(List<T> listToUpdate, List<T> newList,
			boolean updateEvenNotChanged) {

		BiFunction<T, T, Void> notChangedElements = null;
		BiFunction<T, T, Void> changedElements = (oldObj, newObj) -> {

			if (oldObj != null && newObj != null) {
				newObj.updateObject(oldObj);
			}
			return null;
		};

		if (updateEvenNotChanged) {
			notChangedElements = changedElements;
		}
		updateList(listToUpdate, newList, changedElements, notChangedElements);

	}

	public static <T extends IUuidObject> void updateList(List<T> listToUpdate, List<T> newList,
			BiFunction<T, T, Void> changedElementRunnable, BiFunction<T, T, Void> notChangedElementRunnable) {
		IUuidUpdateListProcessor<T> servicesUpdator = new IUuidUpdateListProcessor<T>(listToUpdate, newList) {

			@Override
			public String generateUUid() {
				return null;
			}
		};

		servicesUpdator.compileList();

		List<T> toRemoveGp = servicesUpdator.getToRemoveElements();
		List<T> toAddGp = servicesUpdator.getNewElements();
		List<T> toUpdate = servicesUpdator.getChangedElements();
		List<T> notChanged = servicesUpdator.getNotChangedElements();

		if (listToUpdate != null) {
			if (toRemoveGp != null) {
				listToUpdate.removeAll(toRemoveGp);
			}
			if (toAddGp != null) {
				listToUpdate.addAll(toAddGp);
			}
		}

		Map<String, T> map = putListIntoMap(newList);

		if (toUpdate != null && changedElementRunnable != null) {
			toUpdate.forEach(oldObj -> {

				if (oldObj != null) {
					T newObs = map.get(oldObj.getUuid());
					changedElementRunnable.apply(oldObj, newObs);
				}

			});
		}
		if (notChanged != null && notChangedElementRunnable != null) {
			notChanged.forEach(oldObj -> {

				if (oldObj != null) {
					T newObs = map.get(oldObj.getUuid());
					notChangedElementRunnable.apply(oldObj, newObs);
				}

			});
		}

	}

	public static <T extends ICloneableObject<T>> T cloneObject(T o) {
		if (o != null) {
			return o.cloneObject();
		}
		return null;
	}

	public static <T extends ICloneableObject<T>> List<T> cloneList(List<T> o) {
		return cloneList(o, t -> {

			return cloneObject(t);

		});
	}

	public static <T> T cloneObject(T o, Function<T, T> c) {
		return c.apply(o);
	}

	public static <T> List<T> cloneList(List<T> o, Function<T, T> c) {
		if (o == null) {
			return null;
		}
		ArrayList<T> arl = new ArrayList<>();

		for (T object : o) {
			arl.add(c.apply(object));
		}
		return arl;
	}

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
		return uuidGenerator.generateUuid();
	}

	public static String generateUUIDString(int nbChar) {
		return uuidGenerator.generateUuid(nbChar);
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

	public static List putObjectIntoList(Object sourceObject) {
		ArrayList rl = new ArrayList<>();
		putObjectIntoList(sourceObject, rl);
		return rl;
	}

	public static List putArrayIntoList(Object... sourceList) {
		ArrayList rl = new ArrayList<>();
		putArrayIntoList(sourceList, rl);
		return rl;
	}

	public static void putObjectIntoList(Object sourceObject, List recieverList) {
		if (recieverList != null && sourceObject != null) {
			recieverList.add(sourceObject);
		}
	}

	public static void putArrayIntoList(Object[] sourceList, List recieverList) {
		if (recieverList != null && sourceList != null) {
			for (Object object : sourceList) {
				recieverList.add(object);
			}
		}
	}

	public static void putListIntoList(List sourceList, List recieverList) {
		if (recieverList != null && sourceList != null) {
			for (Object object : sourceList) {
				recieverList.add(object);
			}
		}
	}

	public static List putMapIntoList(Map map) {
		List l = new ArrayList<>();
		putMapIntoList(map, l);
		return l;
	}

	public static void putMapIntoList(Map<String, Object> map, List a) {
		if (a != null && map != null) {

			for (Map.Entry<String, Object> entry : map.entrySet()) {
				Object val = entry.getValue();
				a.add(val);
			}
		}
	}

	public static void putListIntoMap(List a, Map map) {
		if (a != null && map != null) {
			for (Object o : a) {
				if (o != null)
					map.put(((IUuidObject) o).getUuid(), o);
			}
		}
	}

	public static void putArrayIntoMap(Object[] a, Map map) {
		if (a != null && map != null) {
			for (Object o : a) {
				if (o != null)
					map.put(((IUuidObject) o).getUuid(), o);
			}
		}
	}

	public static void putObjectIntoMap(Object a, Map map) {
		if (a != null && map != null) {
			map.put(((IUuidObject) a).getUuid(), a);
		}
	}

	public static Map putObjectIntoMap(Object object) {
		HashMap map = new HashMap<>();
		putObjectIntoMap(object, map);
		return map;
	}

	public static Map putArrayIntoMap(Object... a) {
		HashMap map = new HashMap<>();
		putArrayIntoMap(a, map);
		return map;
	}

	public static Map putListIntoMap(List a) {
		HashMap map = new HashMap<>();
		putListIntoMap(a, map);
		return map;
	}

	public static void removeListFromMap(List a, Map map) {
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

	public static <T> void removeObjectFromList(String uuid, List<T> list) {
		if (list != null && uuid != null) {
			ArrayList<T> arlToRemove = new ArrayList<T>();

			for (T t : list) {
				if (t != null && t instanceof IUuidObject && uuid.equals(((IUuidObject) t).getUuid()))
					arlToRemove.add(t);
			}
			list.removeAll(arlToRemove);
		}
	}

	public static List<String> transformListToStringList(List lst) {
		List<String> arl = new ArrayList<>();
		if (lst != null) {
			for (Object o : lst) {
				if (o != null) {
					if (o instanceof IUuidObject) {
						arl.add(((IUuidObject) o).getUuid());
					} else if (o instanceof String) {
						arl.add((String) o);
					}
				}
			}
		}
		return arl;
	}

	public static String[] transformArrayToStringArray(Object... lst) {
		List<String> arl = new ArrayList<>();
		if (lst != null) {
			for (Object o : lst) {
				if (o != null) {
					if (o instanceof IUuidObject) {
						arl.add(((IUuidObject) o).getUuid());
					} else if (o instanceof String) {
						arl.add((String) o);
					}
				}
			}
		}
		return (String[]) arl.toArray(new String[arl.size()]);
	}

	public static String[] transformListToStringArray(List lst) {
		List<String> arl = transformListToStringList(lst);
		if (arl != null && arl.size() > 0) {
			return (String[]) arl.toArray(new String[arl.size()]);
		}
		return null;
	}

}
