package com.weelgo.eclipse.plugin.job;

import java.util.List;

import com.weelgo.chainmapping.core.CMModuleService;
import com.weelgo.chainmapping.core.CMNode;
import com.weelgo.chainmapping.core.CMTask;
import com.weelgo.core.IProgressMonitor;
import com.weelgo.eclipse.plugin.CMEvents;
import com.weelgo.eclipse.plugin.Factory;
import com.weelgo.eclipse.plugin.ImagesFactory;

public class CMMoveNodesJob extends CMJob {

	public CMMoveNodesJob() {
		super("Move nodes", "Moving nodes ...");
	}

	@Override
	public String getUndoRedoLabel() {
		return "Move nodes";
	}

	@Override
	public boolean isUndoRedoJob() {
		return true;
	}

	@Override
	public String getUndoRedoIcon() {
		return ImagesFactory.SOURCE;
	}

	public static CMMoveNodesJob CREATE() {
		return Factory.create(CMMoveNodesJob.class);
	}

	@Override
	public boolean canExecuteJob() {
		return getSelectedObject(CMNode.class) != null;
	}

	@Override
	public void doRun(IProgressMonitor monitor) {
		List<CMNode> nodes = (List<CMNode>) getSelectedObject();
		if (nodes != null && nodes.size() > 0) {
			CMNode node = nodes.get(0);
			if (node != null) {
				CMModuleService ser = getModuleService(node);
				if (ser != null) {
					setModuleUniqueIdentifier(ser.getModuleUniqueIdentifier());
					ser.moveNodes(nodes);
					sentEvent(CMEvents.NODES_POSITION_CHANGED, nodes);
				}
			}
		}
	}

}
