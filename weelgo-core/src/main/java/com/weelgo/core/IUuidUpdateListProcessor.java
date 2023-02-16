package com.weelgo.core;

import java.util.List;

public abstract class IUuidUpdateListProcessor<T extends IUuidObject> extends UpdateListProcessor<T> {

	public IUuidUpdateListProcessor(List<T> oldTagsArray, List<T> newTagsArray) {
		super(oldTagsArray, newTagsArray);
	}

	@Override
	public String getUuid(T o) {
		if (o != null)
			return o.getUuid();
		return null;
	}

	@Override
	public void setUuid(T o, String uuid) {
		if (o != null) {
			o.setUuid(uuid);
		}
	}

	@Override
	public boolean isEqual(T o1, T o2) {
		if (o1 == null && o2 == null)
			return true;

		if (o1 != null && o2 != null) {
			return CoreUtils.isStrictlyEqualsString(o1.getUuid(), o2.getUuid());
		}

		return false;
	}

}