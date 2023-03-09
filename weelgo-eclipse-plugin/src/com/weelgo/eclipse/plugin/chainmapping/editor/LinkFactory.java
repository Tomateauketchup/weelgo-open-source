package com.weelgo.eclipse.plugin.chainmapping.editor;

import org.eclipse.gef.requests.CreationFactory;

import com.weelgo.chainmapping.core.CMLink;

public class LinkFactory implements CreationFactory {

	@Override
	public Object getNewObject() {
		return new CMLink();
	}

	@Override
	public Object getObjectType() {
		return CMLink.class;
	}

}
