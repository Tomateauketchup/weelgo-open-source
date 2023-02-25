package com.weelgo.eclipse.plugin.handlers;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.di.annotations.Execute;

import com.weelgo.core.CoreUtils;
import com.weelgo.eclipse.plugin.CMService;
import com.weelgo.eclipse.plugin.CurrentSelectionService;
import com.weelgo.eclipse.plugin.job.CMSaveModuleJob;

@Creatable
public class SaveHandler {

	@Execute
	public void execute(CMService cmService, CurrentSelectionService currentSelectionService) {
		String str = currentSelectionService.findModuleUniqueIdentifierObjectId();
		executeWithModuleIdentifier(str);
	}

	public void executeWithModuleIdentifier(String id) {
		if (CoreUtils.isNotNullOrEmpty(id)) {
			CMSaveModuleJob j = CMSaveModuleJob.CREATE();
			j.setModuleUniqueIdentifier(id);
			j.doSchedule();
		}
	}

	@CanExecute
	public boolean canExecute() {
		return true;
	}

}
