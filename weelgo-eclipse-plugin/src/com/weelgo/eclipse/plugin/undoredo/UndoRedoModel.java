package com.weelgo.eclipse.plugin.undoredo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.eclipse.e4.core.di.annotations.Creatable;

import com.weelgo.chainmapping.core.CMModuleService;
import com.weelgo.chainmapping.core.IModuleUniqueIdentifierObject;
import com.weelgo.core.CoreUtils;
import com.weelgo.core.undoredo.UndoRedoManager;
import com.weelgo.core.undoredo.UndoRedoNode;

@Creatable
public class UndoRedoModel implements IModuleUniqueIdentifierObject {

	public static final int ROW_SPACE = 30;
	public static final int COL_SPACE = 45;
	public static final int LEFT_MARGIN = 10;
	public static final int TOP_MARGIN = 5;

	private List<NodeModel> nodes = new ArrayList<>();
	private Map<String, NodeLinkModel> linksMap = new HashMap<>();
	private Map<String, NodeModel> nodesMap = new HashMap<>();
	private Function<Void, CMModuleService> serviceRetriever;

	public void setServiceRetriever(Function<Void, CMModuleService> serviceRetriever) {
		this.serviceRetriever = serviceRetriever;
	}

	public void updateNodes() {
		List<NodeModel> newNodes = new ArrayList<>();
		List<NodeLinkModel> newLinks = new ArrayList<>();

		CMModuleService serv = getModuleService();
		if (serv != null) {
			UndoRedoManager undoRedoManager = serv.getUndoRedoManager();
			if (undoRedoManager != null) {
				UndoRedoNode firstElem = undoRedoManager.getFirstNode();
				if (firstElem != null) {
					UndoRedoNode elemTmp = firstElem;
					NodeModel previousNode = null;
					int nbRow = createNode(newNodes, newLinks, undoRedoManager, elemTmp, previousNode, 0, null,
							serv.getLastSaveDataFingerprint());
					int maxY = nbRow * ROW_SPACE + TOP_MARGIN;
					// On va inverser la pile
					for (NodeModel nd : newNodes) {
						if (nd != null) {
							int y = nd.getPosY();
							y = maxY - y;
							nd.setPosY(y);
						}
					}
				}
			}
		}

		List<NodeModel> toRemove = new ArrayList<>();
		for (Map.Entry<String, NodeModel> entry : nodesMap.entrySet()) {
			String key = entry.getKey();
			NodeModel val = entry.getValue();

			if (!newNodes.contains(val)) {
				toRemove.add(val);
			}
		}
		for (NodeModel n : toRemove) {
			nodesMap.remove(n.getUuid());
		}

		List<NodeLinkModel> toRemoveLnk = new ArrayList<>();
		for (Map.Entry<String, NodeLinkModel> entry : linksMap.entrySet()) {
			String key = entry.getKey();
			NodeLinkModel val = entry.getValue();

			if (!newLinks.contains(val)) {
				toRemoveLnk.add(val);
			}
		}
		for (NodeLinkModel n : toRemoveLnk) {
			linksMap.remove(n.getUuid());
		}

		nodes.clear();
		if (nodesMap.size() > 0) {
			nodes.addAll(nodesMap.values());
		}
//		BiFunction<NodeModel, NodeModel, Void> updateFunc=(oldObj, newObj) -> {
//
//			oldObj.setCurrentNode(newObj.isCurrentNode());
//			oldObj.setPosX(newObj.getPosX());
//			oldObj.setUndoRedoNode(newObj.getUndoRedoNode());
//			oldObj.set
//			return null;
//		};
//		CoreUtils.updateList(nodes, newNodes, updateFunc, updateFunc);

	}

	public int createNode(List<NodeModel> nodes, List<NodeLinkModel> links, UndoRedoManager undoRedoManager,
			UndoRedoNode elem, NodeModel previousNode, int row, List<List<NodeModel>> rows,
			String currentSaveFingerprint) {

		int maxRow = row;
		if (elem != null && undoRedoManager != null) {

			NodeModel n = nodesMap.get(elem.getDataFingerprint());
			if (n == null) {
				n = new NodeModel();
				nodesMap.put(elem.getDataFingerprint(), n);
			}
			n.setParent(null);
			n.getChilds().clear();
			n.setUuid(elem.getDataFingerprint());
			n.setSavedNode(CoreUtils.isStrictlyEqualsString(currentSaveFingerprint, elem.getDataFingerprint()));
			n.setUndoRedoNode(elem);
			n.setCurrentNode(elem.equals(undoRedoManager.getCurrentNode()));

			// We search for a space
			if (rows == null) {
				rows = new ArrayList<>();
			}

			while (rows.size() < row + 1) {
				rows.add(new ArrayList<NodeModel>());
			}

			List<NodeModel> currentRow = rows.get(row);

			int emptyPlace = currentRow.size();
			currentRow.add(n);

			int y = row * ROW_SPACE + TOP_MARGIN;
			int x = emptyPlace * COL_SPACE + LEFT_MARGIN;
			n.setPosX(x);
			n.setPosY(y);

			if (previousNode != null) {

				NodeLinkModel link = new NodeLinkModel();
				link.setSource(previousNode);
				link.setTarget(n);

				NodeLinkModel lnkTmp = linksMap.get(link.getUuid());
				if (lnkTmp == null) {
					linksMap.put(link.getUuid(), link);
				} else {
					link = lnkTmp;
					link.setSource(previousNode);
					link.setTarget(n);
				}

				links.add(link);
				n.setParent(link);
				previousNode.getChilds().add(link);

			}

			nodes.add(n);

			List<UndoRedoNode> childs = elem.getChildNodes();
			if (childs != null) {
				for (UndoRedoNode c : childs) {
					if (c != null) {
						int r = createNode(nodes, links, undoRedoManager, c, n, row + 1, rows, currentSaveFingerprint);
						if (r > maxRow) {
							maxRow = r;
						}
					}
				}
			}
		}

		return maxRow;
	}

	public CMModuleService getModuleService() {
		if (serviceRetriever != null) {
			return serviceRetriever.apply(null);
		}
		return null;
	}

	public List<NodeModel> getNodes() {
		return nodes;
	}

	@Override
	public String getModuleUniqueIdentifier() {

		CMModuleService ser = getModuleService();
		if (ser != null) {
			return ser.getModuleUniqueIdentifier();
		}
		return "";
	}

	@Override
	public void setModuleUniqueIdentifier(String arg0) {
	}

}
