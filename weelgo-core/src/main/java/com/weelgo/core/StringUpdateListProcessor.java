package com.weelgo.core;

import java.util.List;

public abstract class StringUpdateListProcessor extends UpdateListProcessor<String> {

	public StringUpdateListProcessor(List<String> oldTagsArray, List<String> newTagsArray) {
		super(oldTagsArray, newTagsArray);
	}

	@Override
	public String getUuid(String o) {
		return o;
	}

	@Override
	public void setUuid(String o, String uuid) {
	}

	@Override
	public boolean isEqual(String o1, String o2) {
		if (o1 == null && o2 == null)
			return true;

		if (o1 != null && o2 != null) {
			return CoreUtils.isStrictlyEqualsString(o1, o2);
		}

		return false;
	}

}