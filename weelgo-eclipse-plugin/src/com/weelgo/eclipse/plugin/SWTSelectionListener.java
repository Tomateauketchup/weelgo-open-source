package com.weelgo.eclipse.plugin;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public abstract class SWTSelectionListener extends SelectionAdapter {

	public abstract void selected(SelectionEvent e);

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		selected(e);
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		selected(e);
	}

}
