package com.weelgo.eclipse.plugin;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public class ColorFactory {

	public static Color WHITE_COLOR;
	public static Color GREY_COLOR;
	public static Color BLUE_COLOR;
	public static Color BLACK_COLOR;

	static
	{
		WHITE_COLOR=Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
		GREY_COLOR=Display.getCurrent().getSystemColor(SWT.COLOR_GRAY);
		BLUE_COLOR=Display.getCurrent().getSystemColor(SWT.COLOR_BLUE);
		BLACK_COLOR=Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
	}


}
