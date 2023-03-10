package com.weelgo.chainmapping.core;

public class CMNeed extends CMNode<CMNeed> {

	@Override
	public CMNeed createThisObject() {
		return CMFactory.create(CMNeed.class);
	}

}
