package com.weelgo.eclipse.plugin.chainmapping.editor.actions;

import org.eclipse.ui.IWorkbenchPart;

import com.weelgo.chainmapping.core.CMNode;
import com.weelgo.eclipse.plugin.ImagesFactory;
import com.weelgo.eclipse.plugin.job.CMRemoveNodesJob;

public class RemoveNodesAction extends GenericSelectionAction {

	public static String REMOVE_NODES = "remove_nodes";

	public RemoveNodesAction(IWorkbenchPart part) {
		super(part);
		setImageDescriptor(ImagesFactory.getIconsImageDescriptor(ImagesFactory.REMOVE_ICON));
		setText("Remove node(s)");
		setToolTipText("Remove node(s)");
	}

	@Override
	protected void init() {
		super.init();
		setId(REMOVE_NODES);
	}

	@Override
	public void run() {
		CMRemoveNodesJob j = CMRemoveNodesJob.CREATE();
		j.setSelectedObject(findList(CMNode.class));
		CMNode gp = find(CMNode.class);
		if (gp != null) {
			j.setModuleUniqueIdentifier(gp.getModuleUniqueIdentifier());
		}
		j.doSchedule();
	}

}
