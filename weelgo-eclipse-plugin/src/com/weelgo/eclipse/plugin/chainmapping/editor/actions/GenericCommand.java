package com.weelgo.eclipse.plugin.chainmapping.editor.actions;

import org.eclipse.gef.commands.Command;

import com.weelgo.chainmapping.core.CMModuleService;
import com.weelgo.eclipse.plugin.Factory;

public class GenericCommand extends Command {

	public CMModuleService getModelService(String moduleUniqueIdentifier) {
		return Factory.getCMServices().findModuleService(moduleUniqueIdentifier);
	}

}