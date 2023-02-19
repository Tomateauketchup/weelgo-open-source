package com.weelgo.core.undoredo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.weelgo.core.CoreUtils;

public class UndoRedoManager {

	private IUndoRedoModelProvider undoRedoModelProvider;
	private IIncrementalDataWorker incrementalDataWorker = new BasicIncrementalDataWorker();
	private UndoRedoNode currentNode = null;
	private UndoRedoNode firstNode = null;

	public UndoRedoNode saveModel(Object infoData) {
		Object newObject = undoRedoModelProvider.getClonedModel();
		Object oldObject = getReconstitutedData(currentNode);
		Object incrementedData = incrementalDataWorker.createIncrementalData(oldObject, newObject);

		UndoRedoNode n = new UndoRedoNode();
		n.setInfoData(infoData);
		n.setData(incrementedData);
		n.setDataFingerprint(CoreUtils.generateUUIDString());
		if (currentNode != null) {
			n.setParentNode(currentNode);
			currentNode.getChildNodes().add(n);
		}
		currentNode = n;
		if (firstNode == null) {
			firstNode = currentNode;
		}
		undoRedoModelProvider.modelSaved();
		return n;
	}

	public Object restoreModel(List<Object> incrementedDatas) {
		if (incrementedDatas != null && incrementedDatas.size() > 0) {
			Object modelTmp = null;
			for (Object object : incrementedDatas) {
				if (modelTmp == null) {
					modelTmp = object;
				} else {
					modelTmp = incrementalDataWorker.restoreModel(modelTmp, object);
				}
			}
			return modelTmp;
		}
		return null;
	}

	public Object getReconstitutedData(UndoRedoNode node) {
		if (node != null) {

			List<UndoRedoNode> nodes = new ArrayList<>();
			nodes.add(node);
			getAllParentNodes(node, nodes);
			Collections.reverse(nodes);
			List<Object> arl = new ArrayList<>();
			for (UndoRedoNode n : nodes) {
				if (n != null) {
					arl.add(n.getData());
				}
			}
			return restoreModel(arl);
		}
		return null;
	}

	public void getAllParentNodes(UndoRedoNode node, List<UndoRedoNode> lst) {
		if (node != null && lst != null) {
			UndoRedoNode p = node.getParentNode();
			if (p != null) {
				lst.add(p);
				getAllParentNodes(p, lst);
			}
		}
	}

	public UndoRedoNode goToNode(UndoRedoNode node) {
		return null;
	}

	public UndoRedoNode restore() {
		if (currentNode != null) {
			push(currentNode);
			return currentNode;
		}
		return null;
	}

	public void push(UndoRedoNode node) {
		if (node != null) {
			undoRedoModelProvider.pushToModel(getReconstitutedData(node));
		}
	}

	public UndoRedoNode undo() {
		if (currentNode != null && currentNode.getParentNode() != null) {
			currentNode = currentNode.getParentNode();
			return restore();
		}
		return null;
	}

	public UndoRedoNode redo() {
		if (currentNode != null && currentNode.getChildNodes().size() > 0) {
			// We get the last child because is the last save
			currentNode = currentNode.getChildNodes().get(currentNode.getChildNodes().size() - 1);
			push(currentNode);
			return currentNode;
		}
		return null;
	}

	public IUndoRedoModelProvider getUndoRedoModelProvider() {
		return undoRedoModelProvider;
	}

	public void setUndoRedoModelProvider(IUndoRedoModelProvider undoRedoModelProvider) {
		this.undoRedoModelProvider = undoRedoModelProvider;
	}

	public String getCurrentNodeDataFingerprint() {

		if (currentNode != null) {
			return currentNode.getDataFingerprint();
		}
		return "";

	}

	public UndoRedoNode getFirstNode() {
		return firstNode;
	}

	public void setFirstNode(UndoRedoNode firstNode) {
		this.firstNode = firstNode;
	}

	public UndoRedoNode getCurrentNode() {
		return currentNode;
	}

}
