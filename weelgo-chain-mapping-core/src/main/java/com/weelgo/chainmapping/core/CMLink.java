package com.weelgo.chainmapping.core;

import com.weelgo.core.CoreUtils;
import com.weelgo.core.ICloneableObject;
import com.weelgo.core.IUpdatableUuidObject;
import com.weelgo.core.IUuidObject;

public class CMLink implements IUuidObject, IUpdatableUuidObject<CMLink>, ICloneableObject<CMLink> {

	private String sourceUuid;
	private String targetUuid;

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

	@Override
	public void updateObject(CMLink objectToUpdate) {
		populateObject(objectToUpdate);

	}

	@Override
	public void populateObject(CMLink toPopulate) {
		if (toPopulate != null) {
			toPopulate.setSourceUuid(getSourceUuid());
			toPopulate.setTargetUuid(getTargetUuid());
		}
	}

	@Override
	public CMLink createThisObject() {
		return CMFactory.create(CMLink.class);
	}

}
