package com.weelgo.eclipse.plugin.handlers;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

import com.weelgo.eclipse.plugin.Factory;
import com.weelgo.eclipse.plugin.wizard.NewCMModuleWizard;

public class OpenCreateModuleWizardHandler {

	@Execute
	public void execute(IEclipseContext eclipseContext, Shell shell) {

		NewCMModuleWizard wizard = Factory.create(NewCMModuleWizard.class, eclipseContext);
		WizardDialog wd = new WizardDialog(shell, wizard);
		wd.open();
	}

}
