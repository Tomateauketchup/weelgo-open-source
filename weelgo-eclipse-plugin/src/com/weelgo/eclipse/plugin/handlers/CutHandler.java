package com.weelgo.eclipse.plugin.handlers;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;

public class CutHandler {

	@Execute
	public void execute() {
		System.out.println("");
	}

	@CanExecute
	public boolean canExecute() {
		System.out.println("");
		return true;
	}

}
