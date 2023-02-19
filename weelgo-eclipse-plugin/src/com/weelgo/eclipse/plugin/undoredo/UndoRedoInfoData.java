package com.weelgo.eclipse.plugin.undoredo;

public class UndoRedoInfoData {

	private String label;
	private String icon;

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

	public static UndoRedoInfoData create(String label, String icon) {
		UndoRedoInfoData d = new UndoRedoInfoData();
		d.setIcon(icon);
		d.setLabel(label);
		return d;
	}

}
