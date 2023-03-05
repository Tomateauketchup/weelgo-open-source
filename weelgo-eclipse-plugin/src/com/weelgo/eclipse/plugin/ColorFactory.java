package com.weelgo.eclipse.plugin;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public class ColorFactory {

	public static Color WHITE_COLOR;
	public static Color GREY_COLOR;
	public static Color BLUE_COLOR;
	public static Color BLACK_COLOR;
	public static Color GREEN_COLOR;
	public static Color LIGHT_RED_COLOR;
	public static Color MOUSE_OVER_BACKGROUND_COLOR;
	public static Color TOOLTIP_BACKGROUND_COLOR;
	public static Color TOOLTIP_BORDER_COLOR;

	static {
		WHITE_COLOR = Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
		GREY_COLOR = Display.getCurrent().getSystemColor(SWT.COLOR_GRAY);
		BLUE_COLOR = Display.getCurrent().getSystemColor(SWT.COLOR_BLUE);
		BLACK_COLOR = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
		GREEN_COLOR = Display.getCurrent().getSystemColor(SWT.COLOR_GREEN);
		MOUSE_OVER_BACKGROUND_COLOR = new Color(229, 243, 255);
		TOOLTIP_BACKGROUND_COLOR = WHITE_COLOR;
		TOOLTIP_BORDER_COLOR = new Color(118, 118, 118);
		LIGHT_RED_COLOR = new Color(255, 230, 230);
	}

}
