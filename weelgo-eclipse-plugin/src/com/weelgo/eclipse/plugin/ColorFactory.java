package com.weelgo.eclipse.plugin;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public class ColorFactory {

	public static Color TRANSPARENT_COLOR;
	public static Color WHITE_COLOR;
	public static Color GREY_COLOR;
	public static Color BLUE_COLOR;
	public static Color BLACK_COLOR;
	public static Color GREEN_COLOR;
	public static Color RED_COLOR;
	public static Color LIGHT_RED_COLOR;
	public static Color MOUSE_OVER_BACKGROUND_COLOR;
	public static Color TOOLTIP_BACKGROUND_COLOR;
	public static Color TOOLTIP_BORDER_COLOR;
	public static Color NEED_COLOR;
	public static Color TASK_COLOR;

	static {
		TRANSPARENT_COLOR = Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT);
		WHITE_COLOR = Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
		GREY_COLOR = Display.getCurrent().getSystemColor(SWT.COLOR_GRAY);
		BLUE_COLOR = Display.getCurrent().getSystemColor(SWT.COLOR_BLUE);
		BLACK_COLOR = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
		GREEN_COLOR = Display.getCurrent().getSystemColor(SWT.COLOR_GREEN);
		RED_COLOR = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
		MOUSE_OVER_BACKGROUND_COLOR = new Color(229, 243, 255);
		TOOLTIP_BACKGROUND_COLOR = WHITE_COLOR;
		TOOLTIP_BORDER_COLOR = new Color(118, 118, 118);
		LIGHT_RED_COLOR = new Color(255, 230, 230);
		NEED_COLOR = RED_COLOR;
		TASK_COLOR = GREY_COLOR;
	}

	public static Color createColor(com.weelgo.core.Color c) {
		if (c != null) {
			return new Color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
		}
		return null;
	}

	public static void disposeColor(Color c) {
		if (c != null) {
			c.dispose();
		}
	}

	public static Color createTransparentColor() {
		return new Color(TRANSPARENT_COLOR.getRed(), TRANSPARENT_COLOR.getGreen(), TRANSPARENT_COLOR.getBlue(),
				TRANSPARENT_COLOR.getAlpha());
	}

}
