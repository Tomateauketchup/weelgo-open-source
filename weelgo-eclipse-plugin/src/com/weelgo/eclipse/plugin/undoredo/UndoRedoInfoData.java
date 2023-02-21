package com.weelgo.eclipse.plugin.undoredo;

public class UndoRedoInfoData {

	private String label;
	private String icon;
	private String targetName;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getTargetName() {
		return targetName;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	public static UndoRedoInfoData create(String label, String icon, String targetName) {
		UndoRedoInfoData d = new UndoRedoInfoData();
		d.setIcon(icon);
		d.setLabel(label);
		d.setTargetName(targetName);
		return d;
	}

}
