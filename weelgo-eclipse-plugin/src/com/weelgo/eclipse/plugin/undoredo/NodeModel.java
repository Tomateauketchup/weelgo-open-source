package com.weelgo.eclipse.plugin.undoredo;

import java.util.ArrayList;
import java.util.List;

import com.weelgo.core.Constants;
import com.weelgo.core.IUuidObject;
import com.weelgo.core.undoredo.UndoRedoNode;

public class NodeModel implements IUuidObject {
	private String uuid;
	private UndoRedoNode undoRedoNode;
	private boolean currentNode = false;
	private int posX = 0;
	private int posY = 0;
	private boolean savedNode = false;
	private NodeLinkModel parent;
	private List<NodeLinkModel> childs = new ArrayList<NodeLinkModel>();

	public UndoRedoNode getUndoRedoNode() {
		return undoRedoNode;
	}

	public void setUndoRedoNode(UndoRedoNode undoRedoNode) {
		this.undoRedoNode = undoRedoNode;
	}

	public boolean isCurrentNode() {
		return currentNode;
	}

	public void setCurrentNode(boolean currentNode) {
		this.currentNode = currentNode;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public long getTime() {
		if (undoRedoNode != null) {
			return undoRedoNode.getCreationDate();
		}
		return Constants.DATE_TIME_NOT_DEF;
	}

	public int getPosX() {
		return posX;
	}

	public void setPosX(int posX) {
		this.posX = posX;
	}

	public int getPosY() {
		return posY;
	}

	public void setPosY(int posY) {
		this.posY = posY;
	}

	public NodeLinkModel getParent() {
		return parent;
	}

	public void setParent(NodeLinkModel parent) {
		this.parent = parent;
	}

	public List<NodeLinkModel> getChilds() {
		return childs;
	}

	public void setChilds(List<NodeLinkModel> childs) {
		this.childs = childs;
	}

	public boolean isSavedNode() {
		return savedNode;
	}

	public void setSavedNode(boolean savedNode) {
		this.savedNode = savedNode;
	}

	public String getTargetName() {
		UndoRedoInfoData d = getInfoData();
		if (d != null) {
			return d.getTargetName();
		}
		return "";
	}

	public String getLabel() {
		UndoRedoInfoData d = getInfoData();
		if (d != null) {
			return d.getLabel();
		}
		return "";
	}

	public String getIcon() {
		UndoRedoInfoData d = getInfoData();
		if (d != null) {
			return d.getIcon();
		}
		return "";
	}

	public UndoRedoInfoData getInfoData() {
		if (undoRedoNode != null && undoRedoNode.getInfoData() != null
				&& undoRedoNode.getInfoData() instanceof UndoRedoInfoData) {
			return (UndoRedoInfoData) undoRedoNode.getInfoData();
		}
		return null;
	}

}