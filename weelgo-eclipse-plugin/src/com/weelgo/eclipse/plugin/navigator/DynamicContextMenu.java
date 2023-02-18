
package com.weelgo.eclipse.plugin.navigator;

import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;

import com.weelgo.chainmapping.core.CMGroup;
import com.weelgo.chainmapping.core.CMModuleService;
import com.weelgo.eclipse.plugin.CurrentSelectionService;
import com.weelgo.eclipse.plugin.Factory;
import com.weelgo.eclipse.plugin.ImagesFactory;
import com.weelgo.eclipse.plugin.handlers.OpenCreateModuleWizardHandler;

public class DynamicContextMenu {

	@AboutToShow
	public void aboutToShow(List<MMenuElement> items,CurrentSelectionService currentSelectionService) {

		boolean showCreateModule = false;
		boolean showCreateGroup = false;
		boolean showSaveModule=false;
		Object currentSelection = currentSelectionService.getCurrentSelection();
		if (currentSelection != null) {
			if (currentSelection instanceof IContainer) {
				showCreateModule = true;
			}
			if (currentSelection instanceof CMGroup) {
				showCreateGroup = true;
				showCreateModule = true;
			}
		} else {
			showCreateModule = true;
		}
		if(currentSelection instanceof CMModuleService)
		{
			showSaveModule=true;
		}

		if(showSaveModule==false)
		{
			showSaveModule=showCreateGroup;
		}
		
		if (showSaveModule) {
			MHandledMenuItem it = Factory.createMHandledMenuItem("Save Module", ImagesFactory.SAVE_ICON,
					Factory.COMMAND_SAVE_ID);
			items.add(it);

		}
		
		if (showCreateModule) {
			MDirectMenuItem it = Factory.createMDirectMenuItem("Create module", ImagesFactory.CHAIN_MAPPING_ICON,
					OpenCreateModuleWizardHandler.class);
			items.add(it);
		}

		if (showCreateGroup) {
			MHandledMenuItem it = Factory.createMHandledMenuItem("Create group", ImagesFactory.GROUP_ICON,
					Factory.COMMAND_CREATE_CM_GROUP_ID);
			items.add(it);

		}
		
		

//		MHandledMenuItem menuItem = MMenuFactory.INSTANCE.createHandledMenuItem();
//		menuItem.setLabel("Create module");
//
//		MCommand cmd = MCommandsFactory.INSTANCE.createCommand();
//		cmd.setElementId("com.weelgo.eclipse.plugin.handlers.CreateCMGroupCommand");
//		menuItem.setCommand(cmd);
//
//		items.add(menuItem);

	}

}