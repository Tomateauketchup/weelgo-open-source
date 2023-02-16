package com.weelgo.chainmapping.core;

import com.weelgo.core.CoreUtils;
import com.weelgo.core.INamedObject;
import com.weelgo.core.IUuidObject;

public class CMGroup implements IUuidObject, INamedObject, IModuleUniqueIdentifierObject {

	public static final String TYPE_GROUP = "group";
	public static final String TYPE_MODULE = "module";

	private String uuid;
	private String moduleUniqueIdentifier;
	private String packageName;
	private String name;
	private String packageParentPath;
	private String packageFullPath;
	private String groupUuid;
	private String type = TYPE_GROUP;

	public boolean isModule() {
		return TYPE_MODULE.equals(type);
	}

	public boolean isGroup() {
		return !isModule();
	}

	public void calculateUuid(String moduleUniqueIdentifier) {
		setUuid(CoreUtils.createPackageUuid(moduleUniqueIdentifier,
				CoreUtils.createPackageString(packageParentPath, packageName)));
	}

	public void calculatePackageFullPath() {
		setPackageFullPath(CoreUtils.createPackageString(packageParentPath, packageName));
	}

	public void calculateGroupUuid(String moduleUniqueIdentifier) {
		if (CoreUtils.isNotNullOrEmpty(packageParentPath)) {
			setGroupUuid(CoreUtils.createPackageUuid(moduleUniqueIdentifier, packageParentPath));
		} else {
			setGroupUuid(null);
		}

	}

	@Override
	public String getUuid() {
		return uuid;
	}

	@Override
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	public String getModuleUniqueIdentifier() {
		return moduleUniqueIdentifier;
	}

	public void setModuleUniqueIdentifier(String moduleUniqueIdentifier) {
		this.moduleUniqueIdentifier = moduleUniqueIdentifier;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPackageParentPath() {
		return packageParentPath;
	}

	public void setPackageParentPath(String packageParentPath) {
		this.packageParentPath = packageParentPath;
	}

	public String getGroupUuid() {
		return groupUuid;
	}

	public void setGroupUuid(String groupUuid) {
		this.groupUuid = groupUuid;
	}

	public String getPackageFullPath() {
		return packageFullPath;
	}

	public void setPackageFullPath(String packageFullPath) {
		this.packageFullPath = packageFullPath;
	}
	
	@Override
	public String toString() {		
		return getPackageFullPath();
	}

}
