package com.weelgo.chainmapping.core;

import com.weelgo.core.INamedObject;
import com.weelgo.core.IUuidObject;

public class CMChoice implements IUuidObject, INamedObject {

	private String uuid;
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

}
