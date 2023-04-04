package com.weelgo.core;

import java.util.Iterator;

public class Color implements ICloneableObject<Color>, IUpdatablebject<Color> {

	public static Color CREATE_DEFAULT_GROUP_BACKGROUND_COLOR() {
		return new Color(240, 238, 250);
	}

	public static Color CREATE_DEFAULT_GROUP_BORDER_COLOR() {
		return new Color(149, 144, 175);
	}

	public static Color CREATE_BLACK() {
		return new Color(0, 0, 0);
	}

	public static Color CREATE_WHITE() {
		return new Color(255, 255, 255);
	}

	private int red = 0;
	private int green = 0;
	private int blue = 0;
	private int alpha = 0;

	public Color() {
		init(255, 255, 255, 255);
	}

	public Color(int red, int green, int blue) {
		init(red, green, blue, 255);
	}

	public Color(int red, int green, int blue, int alpha) {
		init(red, green, blue, alpha);
	}

	void init(int red, int green, int blue, int alpha) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
	}

	@Override
	public void populateObject(Color toPopulate) {
		if (toPopulate != null) {
			toPopulate.setAlpha(getAlpha());
			toPopulate.setBlue(getBlue());
			toPopulate.setGreen(getGreen());
			toPopulate.setRed(getRed());
		}
	}

	@Override
	public void updateObject(Color objectToUpdate) {
		populateObject(objectToUpdate);
	}

	@Override
	public Color createThisObject() {
		return new Color();
	}

	public int getRed() {
		return red;
	}

	public void setRed(int red) {
		this.red = red;
	}

	public int getGreen() {
		return green;
	}

	public void setGreen(int green) {
		this.green = green;
	}

	public int getBlue() {
		return blue;
	}

	public void setBlue(int blue) {
		this.blue = blue;
	}

	public int getAlpha() {
		return alpha;
	}

	public void setAlpha(int alpha) {
		this.alpha = alpha;
	}

	public static int[] convertIntoArray(Color c) {
		if (c != null) {
			return new int[] { c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha() };
		}
		return convertIntoArray(CREATE_WHITE());

	}

	public static Color convertIntoColor(int[] c) {
		if (c != null && c.length > 3) {
			return new Color(normalize(c[0]), normalize(c[1]), normalize(c[2]), normalize(c[3]));
		}
		return CREATE_WHITE();

	}

	public static int normalize(int i) {
		if (i < 0) {
			i = 0;
		} else if (i > 255) {
			i = 255;
		}
		return i;
	}

	public static Color convertIntoColor(String str) {
		if (str != null) {
			String[] a = str.split(";");
			if (a != null && a.length > 3) {
				int[] ar = new int[4];
				for (int i = 0; i < ar.length; i++) {
					ar[i] = Integer.valueOf(a[i]);
				}
				return convertIntoColor(ar);
			}
		}
		return CREATE_WHITE();
	}

	public static String convertIntoString(Color c) {
		if (c != null) {
			int[] a = convertIntoArray(c);
			if (a != null) {
				String str = "";
				for (int i : a) {

					str = str + i + ";";
				}
				str = CoreUtils.removeEnd(str, ";");
				return str;
			}
		}
		return "";
	}

}
