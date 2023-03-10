
package com.weelgo.eclipse.plugin.navigator;

import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;

import com.weelgo.chainmapping.core.CMGroup;
import com.weelgo.chainmapping.core.CMModuleService;
import com.weelgo.chainmapping.core.CMNode;
import com.weelgo.core.CoreUtils;
import com.weelgo.eclipse.plugin.CurrentSelectionService;
import com.weelgo.eclipse.plugin.Factory;
import com.weelgo.eclipse.plugin.ImagesFactory;
import com.weelgo.eclipse.plugin.handlers.OpenCreateModuleWizardHandler;

public class DynamicContextMenu {

	@AboutToShow
	public void aboutToShow(List<MMenuElement> items, CurrentSelectionService currentSelectionService) {

		boolean showCreateModule = false;
		boolean showCreateGroup = false;
		boolean showSaveModule = false;
		boolean showPutSelectionIntoGroup = false;

		if (currentSelectionService.find(IContainer.class) != null) {
			showCreateModule = true;
		}
		if (currentSelectionService.find(CMGroup.class) != null) {
			showCreateGroup = true;
			showCreateModule = true;
			showSaveModule = true;
		}
		if (currentSelectionService.find(CMModuleService.class) != null) {
			showSaveModule = true;
			showCreateGroup = true;
			showCreateModule = true;
		}

		String moduleUniqueIdentifier = currentSelectionService.findModuleUniqueIdentifierObjectId();
		if (CoreUtils.isNotNullOrEmpty(moduleUniqueIdentifier)) {
			List selectedElements = currentSelectionService
					.findSelectedElementsIntoChainMappingEditor(moduleUniqueIdentifier, CMNode.class);
			if (selectedElements != null && selectedElements.size() > 0) {
				showPutSelectionIntoGroup = true;
			}
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

		if (showPutSelectionIntoGroup) {
			MHandledMenuItem it = Factory.createMHandledMenuItem("Move into this group", null,
					Factory.COMMAND_MOVE_ELEMENTS_INTO_CM_GROUP_ID);
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