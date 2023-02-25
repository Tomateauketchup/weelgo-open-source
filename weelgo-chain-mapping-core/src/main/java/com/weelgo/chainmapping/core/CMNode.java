package com.weelgo.chainmapping.core;

import com.weelgo.core.ICloneableObject;
import com.weelgo.core.INamedObject;
import com.weelgo.core.IUpdatableUuidObject;
import com.weelgo.core.IUuidObject;

public class CMNode<T extends CMNode> implements IUuidObject, INamedObject, IModuleUniqueIdentifierObject,
		ICloneableObject<T>, IUpdatableUuidObject<T> {

	public static final String NAME_TOP = "top";
	public static final String NAME_BOTTOM = "bottom";
	public static final String NAME_RIGHT = "right";
	public static final String NAME_LEFT = "left";

	private String uuid;
	private String moduleUniqueIdentifier;
	private String name;
	private String groupUuid;
	private int positionX;
	private int positionY;
	private String namePosition = NAME_RIGHT;

	@Override
	public T createThisObject() {
		return (T) CMFactory.create(CMNode.class);
	}

	@Override
	public void populateObject(CMNode toPopulate) {
		if (toPopulate != null) {
			toPopulate.setUuid(getUuid());
			toPopulate.setModuleUniqueIdentifier(getModuleUniqueIdentifier());
			toPopulate.setName(getName());
			toPopulate.setGroupUuid(getGroupUuid());
			toPopulate.setPositionX(getPositionX());
			toPopulate.setPositionY(getPositionY());
		}
	}

	@Override
	public void updateObject(CMNode objectToUpdate) {
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
	public String getModuleUniqueIdentifier() {
		return moduleUniqueIdentifier;
	}

	@Override
	public void setModuleUniqueIdentifier(String moduleUniqueIdentifier) {
		this.moduleUniqueIdentifier = moduleUniqueIdentifier;
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

	public int getPositionX() {
		return positionX;
	}

	public void setPositionX(int positionX) {
		this.positionX = positionX;
	}

	public int getPositionY() {
		return positionY;
	}

	public void setPositionY(int positionY) {
		this.positionY = positionY;
	}

	public String getNamePosition() {
		return namePosition;
	}

	public void setNamePosition(String namePosition) {
		this.namePosition = namePosition;
	}

}
