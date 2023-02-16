package com.weelgo.chainmapping.core.json.v1;

import com.weelgo.chainmapping.core.CMGroup;

public class Populator {

	public static void jsonToModelPopulator(Object source, Object toPopulate) {
		if (source instanceof JSN_CMGroup && toPopulate instanceof CMGroup) {
			CMGroup to = (CMGroup) toPopulate;
			JSN_CMGroup src = (JSN_CMGroup) source;
			to.setName(src.getName());
			to.setPackageName(src.getPackage_name());
			to.setPackageParentPath(src.getPackage_parent_path());
			to.setType(src.getType());
		}
	}

	public static void modelToJsonPopulator(Object source, Object toPopulate) {
		if (source instanceof CMGroup && toPopulate instanceof JSN_CMGroup) {
			JSN_CMGroup to = (JSN_CMGroup) toPopulate;
			CMGroup src = (CMGroup) source;
			to.setName(src.getName());
			to.setPackage_name(src.getPackageName());
			to.setPackage_parent_path(src.getPackageParentPath());
			to.setType(src.getType());
		}
	}

}
