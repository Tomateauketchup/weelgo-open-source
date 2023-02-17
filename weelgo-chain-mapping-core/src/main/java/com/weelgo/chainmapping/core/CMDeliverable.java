package com.weelgo.chainmapping.core;

import com.weelgo.core.ICloneableObject;
import com.weelgo.core.INamedObject;
import com.weelgo.core.IUpdatableUuidObject;
import com.weelgo.core.IUuidObject;

public class CMDeliverable implements IUuidObject, INamedObject, IModuleUniqueIdentifierObject,
		ICloneableObject<CMDeliverable>, IUpdatableUuidObject<CMDeliverable> {

	private String uuid;
	private String moduleUniqueIdentifier;
	private String name;
	private String groupUuid;

	@Override
	public void populateObject(CMDeliverable toPopulate) {
		toPopulate.setUuid(getUuid());
		toPopulate.setModuleUniqueIdentifier(getModuleUniqueIdentifier());
		toPopulate.setName(getName());
		toPopulate.setGroupUuid(getGroupUuid());
	}

	@Override
	public CMDeliverable createThisObject() {
		return CMFactory.create(CMDeliverable.class);
	}

	@Override
	public void updateObject(CMDeliverable objectToUpdate) {
		populateObject(objectToUpdate);
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

	public String getGroupUuid() {
		return groupUuid;
	}

	public void setGroupUuid(String groupUuid) {
		this.groupUuid = groupUuid;
	}

	@Override
	public String getModuleUniqueIdentifier() {
		return moduleUniqueIdentifier;
	}

	@Override
	public void setModuleUniqueIdentifier(String moduleUniqueIdentifier) {
		this.moduleUniqueIdentifier = moduleUniqueIdentifier;
	}

}
