package com.weelgo.eclipse.plugin.job;

import java.util.List;

import com.weelgo.chainmapping.core.CMModuleService;
import com.weelgo.chainmapping.core.CMNode;
import com.weelgo.core.CoreUtils;
import com.weelgo.core.IProgressMonitor;
import com.weelgo.eclipse.plugin.CMEvents;
import com.weelgo.eclipse.plugin.Factory;
import com.weelgo.eclipse.plugin.ImagesFactory;

public class CMModifyNodeNamePositionJob extends CMJob {

	public CMModifyNodeNamePositionJob() {
		super("Modify node(s) name position", "Modifying node(s) name position ...");
	}

	@Override
	public String getUndoRedoLabel() {
		return "Modify node(s) name position";
	}

	@Override
	public boolean isUndoRedoJob() {
		return true;
	}

	@Override
	public String getUndoRedoIcon() {
		return ImagesFactory.MODIFY_ICON;
	}

	public static CMModifyNodeNamePositionJob CREATE() {
		return Factory.create(CMModifyNodeNamePositionJob.class);
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
					ser.modifyNodeNamePosition(nodes);
					if (nodes.size() == 1) {
						sentEvent(CMEvents.NODES_NAME_POSITION_CHANGED, node);
					} else {
						sentEvent(CMEvents.NODES_NAME_POSITION_CHANGED, CoreUtils.putListIntoMap(nodes));
					}
				}
			}
		}
	}

}
