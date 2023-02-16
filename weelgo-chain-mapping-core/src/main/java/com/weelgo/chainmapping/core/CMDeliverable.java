package com.weelgo.chainmapping.core;

import com.weelgo.core.INamedObject;
import com.weelgo.core.IUuidObject;

public class CMDeliverable implements IUuidObject, INamedObject, IModuleUniqueIdentifierObject {

	private String uuid;
	private String moduleUniqueIdentifier;
	private String name;
	private String groupUuid;

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
