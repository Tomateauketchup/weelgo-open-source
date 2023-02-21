package com.weelgo.chainmapping.core.navigator;

import com.weelgo.core.INamedObject;
import com.weelgo.core.IUpdatableUuidObject;
import com.weelgo.core.IUuidObject;

public class NavNode implements IUuidObject, INamedObject, IUpdatableUuidObject<NavNode> {

	public static String TYPE_FOLDER = "folder";
	public static String TYPE_GROUP = "group";
	public static String TYPE_MODULE = "module";
	public static String TYPE_SOURCE = "source";
	private String name;
	private String uuid;
	private String parentUuid;
	private String objectType;

	@Override
	public String getUuid() {
		return uuid;
	}

	@Override
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getParentUuid() {
		return parentUuid;
	}

	public void setParentUuid(String parentUuid) {
		this.parentUuid = parentUuid;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	@Override
	public String toString() {
		return "[" + objectType + "]" + uuid;
	}

	@Override
	public void updateObject(NavNode objectToUpdate) {
		if (objectToUpdate != null) {
			objectToUpdate.setName(getName());
			objectToUpdate.setObjectType(getObjectType());
			objectToUpdate.setParentUuid(getParentUuid());
			objectToUpdate.setUuid(getUuid());
		}

	}

}
