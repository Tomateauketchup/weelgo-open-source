package com.weelgo.chainmapping.core;

import com.weelgo.core.ICloneableObject;
import com.weelgo.core.INamedObject;
import com.weelgo.core.IUpdatableUuidObject;
import com.weelgo.core.IUuidObject;

public class CMTask implements IUuidObject, INamedObject, IModuleUniqueIdentifierObject, ICloneableObject<CMTask>,
		IUpdatableUuidObject<CMTask> {

	private String uuid;
	private String moduleUniqueIdentifier;
	private String name;
	private String groupUuid;
	private int posX;
	private int posY;

	@Override
	public void populateObject(CMTask toPopulate) {
		if (toPopulate != null) {
			toPopulate.setUuid(getUuid());
			toPopulate.setModuleUniqueIdentifier(getModuleUniqueIdentifier());
			toPopulate.setName(getName());
			toPopulate.setGroupUuid(getGroupUuid());
			toPopulate.setPosX(getPosX());
			toPopulate.setPosY(getPosY());
		}
	}

	@Override
	public void updateObject(CMTask objectToUpdate) {
		populateObject(objectToUpdate);
	}

	@Override
	public CMTask createThisObject() {
		return CMFactory.create(CMTask.class);
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

	public int getPosX() {
		return posX;
	}

	public void setPosX(int posX) {
		this.posX = posX;
	}

	public int getPosY() {
		return posY;
	}

	public void setPosY(int posY) {
		this.posY = posY;
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
