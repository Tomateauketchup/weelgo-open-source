package com.weelgo.eclipse.plugin.chainmapping.editor.actions;

import java.util.List;

import org.eclipse.ui.IWorkbenchPart;

import com.weelgo.chainmapping.core.CMNode;

public class VAlignNodesAction extends AlignNodesAction {

	public static String V_ALIGN_NODES = "v_align_nodes";

	public VAlignNodesAction(IWorkbenchPart part) {
		super(part);
//		setImageDescriptor(ImagesFactory.getIconsImageDescriptor(ImagesFactory.REMOVE_ICON));
		setText("Vertical align nodes");
		setToolTipText("Vertical align nodes");
	}

	@Override
	protected void init() {
		super.init();
		setId(V_ALIGN_NODES);
	}

	@Override
	public void doAlignment(CMNode refNode, List<CMNode> nodes) {

		if (refNode != null && nodes != null && nodes.size() > 0) {
			sortNodesVertically(nodes);
			for (CMNode n : nodes) {
				if (n != null) {
					n.setPositionX(refNode.getPositionX());
				}
			}
		}

	}

}
