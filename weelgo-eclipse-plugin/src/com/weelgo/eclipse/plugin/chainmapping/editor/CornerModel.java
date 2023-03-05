package com.weelgo.eclipse.plugin.chainmapping.editor;

public class CornerModel {

	public static final String TOP_LEFT = "top_left";
	public static final String TOP_RIGHT = "top_right";
	public static final String BOTTOM_LEFT = "bottom_left";
	public static final String BOTTOM_RIGHT = "bottom_right";

	private String corner = TOP_LEFT;
	private int positionX = 0;
	private int positionY = 0;

	public CornerModel(String corner) {
		this.corner = corner;
	}

	public String getCorner() {
		return corner;
	}

	public void setCorner(String corner) {
		this.corner = corner;
	}

	public int getPositionX() {
		return positionX;
	}

	public void setPositionX(int positionX) {
		this.positionX = positionX;
	}

	public int getPositionY() {
		return positionY;
	}

	public void setPositionY(int positionY) {
		this.positionY = positionY;
	}

}
