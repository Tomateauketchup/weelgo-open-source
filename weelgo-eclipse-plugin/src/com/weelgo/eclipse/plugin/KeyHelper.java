package com.weelgo.eclipse.plugin;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;

public class KeyHelper {

	public static boolean isCTRL_Z(KeyEvent keyEvent) {
		return isKeyPressedWithCTRL(keyEvent, 'z');
	}

	public static boolean isCTRL_Y(KeyEvent keyEvent) {
		return isKeyPressedWithCTRL(keyEvent, 'y');
	}

	public static boolean isCTRL_S(KeyEvent keyEvent) {
		return isKeyPressedWithCTRL(keyEvent, 's');
	}

	public static boolean isENTER(KeyEvent keyEvent) {
		return isKey(keyEvent, SWT.CR);
	}

	public static boolean isESCAPE(KeyEvent keyEvent) {
		return isKey(keyEvent, SWT.ESC);
	}

	public static boolean isKey(KeyEvent keyEvent, char c) {
		if (keyEvent != null) {
			if (keyEvent.keyCode == c) {
				return true;
			}
		}
		return false;
	}

	public static boolean isKeyPressedWithCTRL(KeyEvent keyEvent, char c) {
		if (keyEvent != null) {
			if (Character.toUpperCase((char) keyEvent.keyCode) == Character.toUpperCase(c)
					&& (keyEvent.stateMask & SWT.CTRL) == SWT.CTRL) {
				return true;
			}
		}
		return false;
	}

}
