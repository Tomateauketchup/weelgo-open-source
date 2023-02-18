package com.weelgo.eclipse.plugin.handlers;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;

import com.weelgo.core.CoreUtils;
import com.weelgo.eclipse.plugin.CMService;
import com.weelgo.eclipse.plugin.CurrentSelectionService;
import com.weelgo.eclipse.plugin.job.CMSaveModuleJob;

public class SaveHandler {

	@Execute
	public void execute(CMService cmService, CurrentSelectionService currentSelectionService) {
		String str = cmService.findModuleUniqueIdentifierId(currentSelectionService.getCurrentSelection());
		if (CoreUtils.isNotNullOrEmpty(str)) {
			CMSaveModuleJob j = CMSaveModuleJob.CREATE();
			j.setModuleUniqueIdentifier(str);
			j.doSchedule();
		}
	}

	@CanExecute
	public boolean canExecute() {
		return true;
	}

}
