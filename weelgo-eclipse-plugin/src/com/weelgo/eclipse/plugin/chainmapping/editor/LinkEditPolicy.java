package com.weelgo.eclipse.plugin.chainmapping.editor;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.ReconnectRequest;

import com.weelgo.chainmapping.core.CMNode;
import com.weelgo.eclipse.plugin.chainmapping.editor.actions.CreateLinkCommand;

public class LinkEditPolicy extends GraphicalNodeEditPolicy {

	@Override
	protected Command getConnectionCompleteCommand(CreateConnectionRequest request) {
		CreateLinkCommand result = (CreateLinkCommand) request.getStartCommand();
		CMNode n = (CMNode) getHost().getModel();
		result.setTarget(n);
		return result;
	}

	@Override
	protected Command getConnectionCreateCommand(CreateConnectionRequest request) {
		CreateLinkCommand result = new CreateLinkCommand();
		CMNode n = (CMNode) getHost().getModel();
		result.setSource(n);

		request.setStartCommand(result);
		return result;
	}

	@Override
	protected Command getReconnectTargetCommand(ReconnectRequest request) {
		return null;
	}

	@Override
	protected Command getReconnectSourceCommand(ReconnectRequest request) {
		return null;
	}

}