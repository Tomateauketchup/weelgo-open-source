package com.weelgo.eclipse.plugin.chainmapping.editor.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.ui.IWorkbenchPart;

import com.weelgo.chainmapping.core.CMNode;
import com.weelgo.core.CoreUtils;
import com.weelgo.eclipse.plugin.chainmapping.editor.NodeEditPart;
import com.weelgo.eclipse.plugin.chainmapping.editor.NodeFigure;

public class PackAlignNodesAction extends AlignNodesAction {

	public static String PACK_ALIGN_NODES = "pack_align_nodes";

	public PackAlignNodesAction(IWorkbenchPart part) {
		super(part);
//		setImageDescriptor(ImagesFactory.getIconsImageDescriptor(ImagesFactory.REMOVE_ICON));
		setText("Pack align nodes");
		setToolTipText("Pack align nodes");
	}

	@Override
	protected void init() {
		super.init();
		setId(PACK_ALIGN_NODES);
	}

	@Override
	public void doAlignment(CMNode refNode, List<CMNode> nodes) {

		sortNodesVertically(nodes);

		// 1 on fait un magic vertical aligment
		List<CMNode> nodesListTmp = new ArrayList<CMNode>();
		if (nodes != null) {
			nodesListTmp.addAll(nodes);
		}

		ArrayList<Col> cols = new ArrayList<PackAlignNodesAction.Col>();
		int searchSize = 20;
		do {

			Col colTmp = null;
			List<CMNode> nodesToRemove = new ArrayList<CMNode>();
			for (CMNode n : nodesListTmp) {
				if (n != null) {
					if (colTmp == null) {
						colTmp = new Col();
						colTmp.posX = n.getPositionX();
						cols.add(colTmp);
					}
					int posX = n.getPositionX();
					if (posX > colTmp.posX - searchSize && posX < colTmp.posX + searchSize) {
						colTmp.nodes.add(n);
						nodesToRemove.add(n);
					}
				}
			}
			nodesListTmp.removeAll(nodesToRemove);

		} while (nodesListTmp.size() > 0);

		if (cols.size() > 0) {

			Map<String, NodeEditPart> nodesPart = getNodeParts();
			Collections.sort(cols, new Comparator<Col>() {

				@Override
				public int compare(Col o1, Col o2) {
					if (o1 != null && o2 != null) {
						return Integer.compare(o1.posX, o2.posX);
					}
					return 0;
				}
			});

			Col prevCol = null;
			for (Col col : cols) {
				if (col != null) {

					int minX = Integer.MAX_VALUE;
					int maxX = Integer.MIN_VALUE;
					for (CMNode n : col.nodes) {
						if (n != null) {

							int nodeDelta = col.posX - n.getPositionX();
							Rectangle bounds = getNodeBounds(nodesPart, n);
							if (bounds != null) {
								int minXTmp = bounds.x + nodeDelta;
								int maxXTmp = bounds.x + bounds.width + nodeDelta;
								if (minX > minXTmp) {
									minX = minXTmp;
								}
								if (maxX < maxXTmp) {
									maxX = maxXTmp;
								}
							}
						}
					}
					col.maxX = maxX;
					col.minX = minX;

					if (prevCol != null) {
						// Il faut shifter la colonne
						int delta = col.minX - prevCol.maxX;
						col.minX = col.minX - delta + H_MARGIN;
						col.maxX = col.maxX - delta + H_MARGIN;
						col.posX = col.posX - delta + H_MARGIN;
					}

					prevCol = col;
				}
			}
			int topRef = Integer.MIN_VALUE;
			for (Col col : cols) {
				if (col != null) {
					int currentTop = Integer.MIN_VALUE;
					;
					for (CMNode n : col.nodes) {
						if (n != null) {
							Rectangle bounds = getNodeBounds(nodesPart, n);
							if (bounds != null) {
								if (topRef == Integer.MIN_VALUE) {
									topRef = bounds.y;
								}
								if (currentTop == Integer.MIN_VALUE) {
									currentTop = topRef;
								}
								int posY = currentTop;
								if (CMNode.NAME_LEFT.equals(n.getNamePosition())
										|| CMNode.NAME_RIGHT.equals(n.getNamePosition())) {
									posY = posY + Math.round((float) bounds.height / 2f);
								} else if (CMNode.NAME_TOP.equals(n.getNamePosition())) {
									posY = posY + bounds.height - Math.round((float) NodeFigure.NODE_SIZE / 2f);
								} else if (CMNode.NAME_BOTTOM.equals(n.getNamePosition())) {
									posY = posY + Math.round((float) NodeFigure.NODE_SIZE / 2f);
								}
								n.setPositionY(posY);
								n.setPositionX(col.posX);
								currentTop = currentTop + bounds.height + V_MARGIN;
							}
						}
					}
				}

			}
		}
	}

	private class Col {
		public int posX;
		public List<CMNode> nodes = new ArrayList<CMNode>();
		public int minX = 0;
		public int maxX = 0;
	}

}
