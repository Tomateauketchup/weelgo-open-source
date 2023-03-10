package com.weelgo.chainmapping.core;

import com.weelgo.core.CoreUtils;
import com.weelgo.core.ICloneableObject;
import com.weelgo.core.IUpdatableUuidObject;
import com.weelgo.core.IUuidObject;

public class CMLink
		implements IUuidObject, IUpdatableUuidObject<CMLink>, ICloneableObject<CMLink>, IModuleUniqueIdentifierObject {

	private String moduleUniqueIdentifier;
	private String sourceUuid;
	private String targetUuid;

	@Override
	public void updateObject(CMLink objectToUpdate) {
		populateObject(objectToUpdate);

	}

	@Override
	public void populateObject(CMLink toPopulate) {
		if (toPopulate != null) {
			toPopulate.setSourceUuid(getSourceUuid());
			toPopulate.setTargetUuid(getTargetUuid());
			toPopulate.setModuleUniqueIdentifier(getModuleUniqueIdentifier());
		}
	}

	@Override
	public CMLink createThisObject() {
		return CMFactory.create(CMLink.class);
	}

	public String getModuleUniqueIdentifier() {
		return moduleUniqueIdentifier;
	}

	public void setModuleUniqueIdentifier(String moduleUniqueIdentifier) {
		this.moduleUniqueIdentifier = moduleUniqueIdentifier;
	}

	public String getSourceUuid() {
		return sourceUuid;
	}

	public void setSourceUuid(String sourceUuid) {
		this.sourceUuid = sourceUuid;
	}

	public String getTargetUuid() {
		return targetUuid;
	}

	public void setTargetUuid(String targetUuid) {
		this.targetUuid = targetUuid;
	}

	@Override
	public String getUuid() {

		return CoreUtils.getLinkUUID(sourceUuid, targetUuid);
	}

	@Override
	public void setUuid(String uuid) {

	}

}
