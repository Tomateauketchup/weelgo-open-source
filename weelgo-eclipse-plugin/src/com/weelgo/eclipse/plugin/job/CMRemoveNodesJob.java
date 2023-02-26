package com.weelgo.eclipse.plugin.job;

import java.util.List;

import com.weelgo.chainmapping.core.CMModuleService;
import com.weelgo.chainmapping.core.CMNode;
import com.weelgo.core.CoreUtils;
import com.weelgo.core.IProgressMonitor;
import com.weelgo.eclipse.plugin.CMEvents;
import com.weelgo.eclipse.plugin.Factory;
import com.weelgo.eclipse.plugin.ImagesFactory;

public class CMRemoveNodesJob extends CMJob {

	public CMRemoveNodesJob() {
		super("Remove node(s)", "Removing node(s) ...");
	}

	@Override
	public String getUndoRedoLabel() {
		return "Remove node(s)";
	}

	@Override
	public boolean isUndoRedoJob() {
		return true;
	}

	@Override
	public String getUndoRedoIcon() {
		return ImagesFactory.REMOVE_ICON;
	}

	public static CMRemoveNodesJob CREATE() {
		return Factory.create(CMRemoveNodesJob.class);
	}

	@Override
	public boolean canExecuteJob() {
		List<CMNode> selection = (List<CMNode>) getSelectedObject();

		if (selection != null && selection.size() > 0) {
			return true;
		}

		return false;

	}

	@Override
	public void doRun(IProgressMonitor monitor) {

		List<CMNode> selection = (List<CMNode>) getSelectedObject();
		if (selection != null && selection.size() > 0) {
			CMNode node = selection.get(0);
			if (node != null) {
				CMModuleService ser = getModuleService(node);
				if (ser != null) {
					setModuleUniqueIdentifier(node.getModuleUniqueIdentifier());
					String[] uuids = CoreUtils.transformListToStringArray(selection);
					if (uuids != null && uuids.length > 0) {
						ser.removeNodes(uuids);
						sentEvent(CMEvents.NODES_REMOVED, selection);
					}
				}
			}
		}
	}

}
