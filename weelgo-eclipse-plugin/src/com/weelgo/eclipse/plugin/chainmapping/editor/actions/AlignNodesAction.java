package com.weelgo.eclipse.plugin.chainmapping.editor.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.ui.IWorkbenchPart;

import com.weelgo.chainmapping.core.CMNode;
import com.weelgo.eclipse.plugin.chainmapping.editor.ChainMappingEditor;
import com.weelgo.eclipse.plugin.chainmapping.editor.NodeEditPart;
import com.weelgo.eclipse.plugin.job.CMMoveNodesJob;

public abstract class AlignNodesAction extends GenericSelectionAction {

	public static final int V_MARGIN = 5;
	public static final int H_MARGIN = 20;

	public AlignNodesAction(IWorkbenchPart part) {
		super(part);
	}

	@Override
	public void run() {
		CMMoveNodesJob j = CMMoveNodesJob.CREATE();

		List<CMNode> nodes = findList(CMNode.class);
		List<CMNode> newList = new ArrayList<>();
		CMNode node = null;
		if (nodes != null) {
			for (CMNode n : nodes) {
				if (n != null) {
					CMNode newNode = (CMNode) n.cloneObject();
					newList.add(newNode);
					if (node == null) {
						node = newNode;
					}
				}
			}
		}

		doAlignment(node, newList);
		j.setSelectedObject(newList);
		if (node != null) {
			j.setModuleUniqueIdentifier(node.getModuleUniqueIdentifier());
		}
		j.doSchedule();
	}

	public abstract void doAlignment(CMNode refNode, List<CMNode> nodes);

	public void sortNodesVertically(List<CMNode> nodes) {
		if (nodes != null && nodes.size() > 0) {
			Collections.sort(nodes, new Comparator<CMNode>() {

				@Override
				public int compare(CMNode o1, CMNode o2) {
					if (o1 != null && o2 != null) {
						return Integer.compare(o1.getPositionY(), o2.getPositionY());
					}
					return 0;
				}
			});
		}
	}

	public Map<String, NodeEditPart> getNodeParts() {
		ChainMappingEditor p = (ChainMappingEditor) getWorkbenchPart();
		return p.getEditorEditPart().getNodeParts();
	}

	public Rectangle getNodeBounds(Map<String, NodeEditPart> parts, CMNode n) {
		if (n != null && parts != null) {
			NodeEditPart pr = parts.get(n.getUuid());
			if (pr != null && pr.getFigure() != null) {
				return pr.getFigure().getBounds();
			}
		}
		return null;
	}
}
