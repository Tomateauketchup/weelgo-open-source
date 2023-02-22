package com.weelgo.chainmapping.core.navigator;

import com.weelgo.chainmapping.core.IDataSourceObject;
import com.weelgo.core.INamedObject;
import com.weelgo.core.IUpdatableUuidObject;
import com.weelgo.core.IUuidObject;

public class NavNode implements IUuidObject, INamedObject, IUpdatableUuidObject<NavNode>, IDataSourceObject {

	public static String TYPE_FOLDER = "folder";
	public static String TYPE_GROUP = "group";
	public static String TYPE_MODULE = "module";
	public static String TYPE_SOURCE = "source";
	public static String TYPE_PROJECT = "project";
	private String name;
	private String uuid;
	private String parentUuid;
	private String objectType;
	private Object data;
	private String dataSourceUuid;
	private boolean dirty = false;
	private boolean submodulesDirty = false;

	@Override
	public void updateObject(NavNode objectToUpdate) {
		if (objectToUpdate != null) {
			objectToUpdate.setName(getName());
			objectToUpdate.setObjectType(getObjectType());
			objectToUpdate.setParentUuid(getParentUuid());
			objectToUpdate.setUuid(getUuid());
			objectToUpdate.setData(getData());
			objectToUpdate.setDataSourceUuid(getDataSourceUuid());
			objectToUpdate.setDirty(isDirty());
			objectToUpdate.setSubmodulesDirty(isSubmodulesDirty());
		}

	}

	public boolean isFolder() {
		return TYPE_FOLDER.equals(objectType);
	}

	public boolean isModule() {
		return TYPE_MODULE.equals(objectType);
	}

	public boolean isGroup() {
		return TYPE_GROUP.equals(objectType);
	}

	public boolean isSource() {
		return TYPE_SOURCE.equals(objectType);
	}

	public boolean isProject() {
		return TYPE_PROJECT.equals(objectType);
	}

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

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public boolean isDirty() {
		return dirty;
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	public boolean isSubmodulesDirty() {
		return submodulesDirty;
	}

	public void setSubmodulesDirty(boolean submodulesDirty) {
		this.submodulesDirty = submodulesDirty;
	}

	@Override
	public String getDataSourceUuid() {
		return dataSourceUuid;
	}

	@Override
	public void setDataSourceUuid(String dataSourceUuid) {
		this.dataSourceUuid = dataSourceUuid;
	}

	@Override
	public String toString() {
		return "[" + objectType + "]" + uuid;
	}

}
