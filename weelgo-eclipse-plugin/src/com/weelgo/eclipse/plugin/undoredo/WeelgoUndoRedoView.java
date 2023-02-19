package com.weelgo.eclipse.plugin.undoredo;

import javax.inject.Inject;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class WeelgoUndoRedoView extends ViewPart {

	@Inject
	WeelgoUndoRedoViewPart part;


	@Override
	public void createPartControl(Composite parent) {

		part.postConstruct(parent);

	}

	@Override
	public void setFocus() {


	}

}
