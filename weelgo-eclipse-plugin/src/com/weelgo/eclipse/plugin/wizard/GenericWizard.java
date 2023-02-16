package com.weelgo.eclipse.plugin.wizard;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.wizard.Wizard;

import com.weelgo.eclipse.plugin.Factory;
import com.weelgo.eclipse.plugin.job.CMJob;

public abstract class GenericWizard extends Wizard {

	private Object currentSelection;

	public GenericWizard() {
		setCurrentSelection(Factory.getCurrentSelection());
	}

	@Override
	public boolean performFinish() {
		ArrayList<CMJob> arl = new ArrayList<>();
		createFinishJob(arl);
		return runJobs(arl);
	}

	public abstract void createFinishJob(List<CMJob> jobList);

	public boolean runJobs(List<CMJob> jobList) {

		if (jobList != null) {
			for (CMJob cmJob : jobList) {
				if (cmJob != null) {
					cmJob.doSchedule();
				}
			}
		}
		return true;
	}

	public Object getCurrentSelection() {
		return currentSelection;
	}

	public void setCurrentSelection(Object currentSelection) {
		this.currentSelection = currentSelection;
	}

}
