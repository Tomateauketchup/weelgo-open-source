package com.weelgo.chainmapping.core;

import com.weelgo.core.CoreUtils;
import com.weelgo.core.ICloneableObject;
import com.weelgo.core.INamedObject;
import com.weelgo.core.IUpdatableUuidObject;
import com.weelgo.core.IUuidObject;

public class CMGroup implements
		IUuidObject, INamedObject, IModuleUniqueIdentifierObject, ICloneableObject<CMGroup>,
IUpdatableUuidObject<CMGroup> {

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

	public void calculatePackageFullPath() {
		setPackageFullPath(CoreUtils.createPackageString(packageParentPath, packageName));
	}	

	@Override
	public void populateObject(CMGroup gp) {
		if (gp != null) {
			gp.setUuid(getUuid());
			gp.setModuleUniqueIdentifier(getModuleUniqueIdentifier());
			gp.setPackageName(getPackageName());
			gp.setName(getName());
			gp.setPackageParentPath(getPackageParentPath());
			gp.setPackageFullPath(getPackageFullPath());
			gp.setGroupUuid(getGroupUuid());
			gp.setType(getType());
		}
	}

	@Override
	public void updateObject(CMGroup objectToUpdate) {
		populateObject(objectToUpdate);
	}

	@Override
	public CMGroup createThisObject() {
		return CMFactory.create(CMGroup.class);
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

	@Override
	public String getModuleUniqueIdentifier() {
		return moduleUniqueIdentifier;
	}

	@Override
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
