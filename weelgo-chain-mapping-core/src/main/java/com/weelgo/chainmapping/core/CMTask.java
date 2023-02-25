package com.weelgo.chainmapping.core;

public class CMTask extends CMNode<CMTask> {

	@Override
	public CMTask createThisObject() {
		return CMFactory.create(CMTask.class);
	}	
}
