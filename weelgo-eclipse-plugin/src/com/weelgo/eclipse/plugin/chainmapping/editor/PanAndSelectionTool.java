package com.weelgo.eclipse.plugin.chainmapping.editor;

import java.lang.reflect.Field;

import org.eclipse.gef.tools.PanningSelectionTool;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weelgo.core.exceptions.ExceptionsUtils;

public class PanAndSelectionTool extends PanningSelectionTool {

	private static Logger logger = LoggerFactory.getLogger(PanAndSelectionTool.class);

	private static final int panButton = 2;

	@Override
	protected boolean handleButtonDown(int which) {
		if (which == panButton) {
			which = 1;
			Event ev = new Event();
			ev.widget = getCurrentViewer().getControl();
			KeyEvent e = new KeyEvent(ev);
			e.character = ' ';
			handleKeyDown(e);
//			setSpaceBarDown(true);

		}
		return super.handleButtonDown(which);
	}

	@Override
	protected boolean handleButtonUp(int which) {
		if (which == panButton) {
			which = 1;
			Event ev = new Event();
			ev.widget = getCurrentViewer().getControl();
			KeyEvent e = new KeyEvent(ev);
			e.character = ' ';
			handleKeyUp(e);
//			setSpaceBarDown(false);
		}
		return super.handleButtonUp(which);
	}

	public void setSpaceBarDown(boolean down) {
		Field privateField;
		try {
			privateField = PanningSelectionTool.class.getDeclaredField("isSpaceBarDown");

			// Set the accessibility as true
			privateField.setAccessible(true);

			// Store the value of private field in variable
			privateField.setBoolean(this, down);

		} catch (Exception e) {
			ExceptionsUtils.ManageException(e, null);
		}

	}

}
