package com.weelgo.core.undoredo;

import java.util.ArrayList;
import java.util.List;

import com.weelgo.core.INamedObject;

public class UndoRedoNode implements INamedObject {

	private String name;
	private UndoRedoNode parentNode;
	private List<UndoRedoNode> childNodes = new ArrayList<>();
	private Object data;
	private String dataFingerprint;

	public String getDataFingerprint() {
		return dataFingerprint;
	}

	public void setDataFingerprint(String dataFingerprint) {
		this.dataFingerprint = dataFingerprint;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public UndoRedoNode getParentNode() {
		return parentNode;
	}

	public void setParentNode(UndoRedoNode parentNode) {
		this.parentNode = parentNode;
	}

	public List<UndoRedoNode> getChildNodes() {
		return childNodes;
	}

	public void setChildNodes(List<UndoRedoNode> childNodes) {
		this.childNodes = childNodes;
	}

}
