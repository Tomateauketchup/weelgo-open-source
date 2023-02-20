package com.weelgo.eclipse.plugin.undoredo;

import com.weelgo.core.CoreUtils;
import com.weelgo.core.IUuidObject;

public class NodeLinkModel implements IUuidObject {

	private NodeModel source;
	private NodeModel target;

	public NodeModel getSource() {
		return source;
	}

	public void setSource(NodeModel source) {
		this.source = source;
	}

	public NodeModel getTarget() {
		return target;
	}

	public void setTarget(NodeModel target) {
		this.target = target;
	}

	@Override
	public String getUuid() {
		if (source != null && target != null) {
			return CoreUtils.getLinkUUID(source.getUuid(), target.getUuid());
		}
		return "";
	}

	@Override
	public void setUuid(String arg0) {	

	}

}
