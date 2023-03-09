package com.weelgo.eclipse.plugin.job;

import java.util.Map;

import com.weelgo.chainmapping.core.CMLink;
import com.weelgo.chainmapping.core.CMModuleService;
import com.weelgo.chainmapping.core.CMNode;
import com.weelgo.core.CoreUtils;
import com.weelgo.core.IProgressMonitor;
import com.weelgo.eclipse.plugin.CMEvents;
import com.weelgo.eclipse.plugin.Factory;
import com.weelgo.eclipse.plugin.ImagesFactory;

public class CMCreateLinkJob extends CMJob {

	private CMNode source;
	private CMNode target;

	public CMCreateLinkJob() {
		super("Create link", "Creating link ...");
	}

	@Override
	public String getUndoRedoLabel() {
		return "Create link";
	}

	@Override
	public boolean isUndoRedoJob() {
		return true;
	}

	@Override
	public String getUndoRedoIcon() {
		return ImagesFactory.MODIFY_ICON;
	}

	public static CMCreateLinkJob CREATE() {
		return Factory.create(CMCreateLinkJob.class);
	}

	@Override
	public boolean canExecuteJob() {
		return source != null && target != null;
	}

	@Override
	public void doRun(IProgressMonitor monitor) {

		if (source != null && target != null) {
			CMModuleService ser = getModuleService(target);
			if (ser != null) {
				setModuleUniqueIdentifier(ser.getModuleUniqueIdentifier());
				CMLink lnk = ser.linkNodes(source.getUuid(), target.getUuid());
				Map map = CoreUtils.putArrayIntoMap(source, target, lnk);
				sentEvent(CMEvents.LINK_CREATED, map);
			}
		}
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
