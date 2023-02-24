package com.weelgo.eclipse.plugin;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.DefaultToolTip;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weelgo.eclipse.plugin.chainmapping.editor.ChainMappingEditor;
import com.weelgo.eclipse.plugin.chainmapping.editor.ChainMappingEditorInput;
import com.weelgo.eclipse.plugin.job.CMJob;
import com.weelgo.eclipse.plugin.job.CMLoadAllModulesJob;

public class TestButtonHandler {

	private static Logger logger = LoggerFactory.getLogger(TestButtonHandler.class);

	public static int i = 0;

	// the following code assumes that
	// the "com.vogella.tasks.ui.partdescriptor.fileeditor" ID
	// is used for the part descriptor
	@Execute
	public void execute(EPartService partService, EModelService modelService, MApplication application, MWindow window,
			CMService myService, UISynchronize sync, @Named(IServiceConstants.ACTIVE_SHELL) Shell shell,
			IEventBroker eventBroker) {

//		DefaultToolTip tp=new DefaultToolTip(null);
//		tp.setText("toto");
//		tp.show(Display.getDefault().getCursorLocation());

//		CMLoadAllModulesJob job = CMLoadAllModulesJob.CREATE();
//		job.doSchedule();

//		CMJob j = new CMJob("Mon truc à faire") {
//			@Override
//			public void doRun(com.weelgo.core.IProgressMonitor monitor) {
//				sync.asyncExec(() -> {
//
//					MessageDialog.openInformation(shell, "Truc fini", "Le truc que tu as demandé a été fini");
//
//				});
//			}
//		};
//		j.setUser(true);
//		j.schedule();

//		CMJob.loadModules();
//		MPartStack partStack = (MPartStack)modelService.find("org.eclipse.e4.primaryDataStack", application);
//
//
//
//		// create a new part based on a part descriptor
//		// if multiple parts of this type are allowed a new part
//		// is always generated
//		i++;
//		MPlaceholder o = modelService.findPlaceholderFor(window, application);
//		MPart part = partService.createPart("com.weelgo.eclipse.plugin.chainmapping.editor.ChainMappingEditorPart");
//		part.setLabel("New Dynamic Part " + i);
//		part.getPersistedState().put("some key", String.valueOf(i));
//		partStack.getChildren().add(part);
//		// the provided part is be shown
//		partService.showPart(part, PartState.ACTIVATE);
		
	}
}
