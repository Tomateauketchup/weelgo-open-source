package com.weelgo.eclipse.plugin.chainmapping.editor.actions;

import com.weelgo.chainmapping.core.CMNode;
import com.weelgo.eclipse.plugin.job.CMCreateLinkJob;

public class CreateLinkCommand extends GenericCommand {

	private CMNode source;
	private CMNode target;

	@Override
	public void execute() {
		CMCreateLinkJob j = CMCreateLinkJob.CREATE();
		j.setSource(source);
		j.setTarget(target);
		j.doSchedule();
	}

	@Override
	public boolean canExecute() {
		return source != null && target != null;
	}

	public CMNode getSource() {
		return source;
	}

	public void setSource(CMNode source) {
		this.source = source;
	}

	public CMNode getTarget() {
		return target;
	}

	public void setTarget(CMNode target) {
		this.target = target;
	}

}