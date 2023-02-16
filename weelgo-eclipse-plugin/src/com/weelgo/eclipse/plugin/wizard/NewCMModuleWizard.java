package com.weelgo.eclipse.plugin.wizard;

import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weelgo.core.exceptions.ExceptionsUtils;
import com.weelgo.eclipse.plugin.Factory;
import com.weelgo.eclipse.plugin.job.CMCreateModuleJob;
import com.weelgo.eclipse.plugin.job.CMJob;
import com.weelgo.eclipse.plugin.job.CMLoadAllModulesJob;
import com.weelgo.eclipse.plugin.job.CMOpenChainMappingEditorJob;

/**
 * This is a sample new wizard. Its role is to create a new file resource in the
 * provided container. If the container resource (a folder or a project) is
 * selected in the workspace when the wizard is opened, it will accept it as the
 * target container. The wizard creates one file with the extension "cmm". If a
 * sample multi-page editor (also available as a template) is registered for the
 * same extension, it will be able to open it.
 */

public class NewCMModuleWizard extends GenericWizard implements INewWizard {

	private static Logger logger = LoggerFactory.getLogger(NewCMModuleWizard.class);
	private NewCMModuleWizardPage page;
	private ISelection selection;

	/**
	 * Constructor for NewCMModuleWizard.
	 */
	public NewCMModuleWizard() {
		super();
		setNeedsProgressMonitor(true);
		setWindowTitle("Create chain-mapping module");

	}

	/**
	 * Adding the page to the wizard.
	 */
	@Override
	public void addPages() {
		page = new NewCMModuleWizardPage(selection, getCurrentSelection());
		addPage(page);
	}

	@Override
	public void createFinishJob(List<CMJob> jobList) {
		final String containerName = page.getContainerName();
		final String moduleName = page.geModuleName();
		final String packageName = page.getPackageName();

		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();//
		IResource resource = root.findMember(new Path(containerName));

		if (!resource.exists() || !(resource instanceof IContainer)) {
			ExceptionsUtils.throwException("Container \"" + containerName + "\" does not exist.");
		}
		IContainer container = (IContainer) resource;

		CMCreateModuleJob job = CMCreateModuleJob.CREATE();
		job.setModuleName(moduleName);
		job.setModulePackage(packageName);
		job.setFolderContainer(container);
		jobList.add(job);

		CMLoadAllModulesJob loadJob = CMLoadAllModulesJob.CREATE();
		jobList.add(loadJob);

		CMOpenChainMappingEditorJob openJob = CMOpenChainMappingEditorJob.CREATE();
		jobList.add(openJob);
	}

	/**
	 * We will accept the selection in the workbench to see if we can initialize
	 * from it.
	 *
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}
}