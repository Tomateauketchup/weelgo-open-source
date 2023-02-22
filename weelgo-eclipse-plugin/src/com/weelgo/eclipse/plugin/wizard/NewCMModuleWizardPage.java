package com.weelgo.eclipse.plugin.wizard;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

import com.weelgo.chainmapping.core.CMGroup;
import com.weelgo.core.Constants;
import com.weelgo.core.CoreUtils;
import com.weelgo.core.ValidatorUtils;
import com.weelgo.eclipse.plugin.CMService;
import com.weelgo.eclipse.plugin.Factory;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (cmm).
 */

public class NewCMModuleWizardPage extends WizardPage {
	private Text containerText;

	private Text moduleNameText;
	private Text modulePackageNameText;

	private ISelection selection;
	private Object selection2;
	private String dataSourceUuid;

	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public NewCMModuleWizardPage(ISelection selection, Object selection2) {
		super("wizardPage");
		setTitle("Create chain-mapping module");
		setDescription("This wizard creates a new chain-mapping module.");
		this.selection = selection;
		this.selection2 = selection2;
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 9;
		Label label = new Label(container, SWT.NULL);
		label.setText("&Container:");

		containerText = new Text(container, SWT.BORDER | SWT.SINGLE | SWT.READ_ONLY);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		containerText.setLayoutData(gd);
		containerText.addModifyListener(e -> dialogChanged());

		Button button = new Button(container, SWT.PUSH);
		button.setText("Browse...");
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleBrowse();
			}
		});

		label = new Label(container, SWT.NULL);
		label.setText("&Name:");

		moduleNameText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		moduleNameText.setLayoutData(gd);
		moduleNameText.addModifyListener(e -> dialogChanged());

		// Empty celllabel = new Label(container, SWT.NULL);
		new Label(container, SWT.NULL);

		label = new Label(container, SWT.NULL);
		label.setText("&Package name:");

		modulePackageNameText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		modulePackageNameText.setLayoutData(gd);
		modulePackageNameText.addModifyListener(e -> dialogChanged());

		initialize();
		dialogChanged();
		setControl(container);
	}

	/**
	 * Tests if the current workbench selection is a suitable container to use.
	 */

	private void initialize() {
		Object obj = null;
		if (selection != null && selection.isEmpty() == false && selection instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) selection;
			if (ssel.size() > 1)
				return;
			obj = ssel.getFirstElement();
			dataSourceUuid = CMService.ECLIPSE_WORKSPACE_DATA_SOURCE_UUID;
		}
		if (obj == null) {
			obj = selection2;
			dataSourceUuid = Factory.getSelectionAdapter().findDataSourceUuid(obj);
		}
		if (obj != null) {

			IResource res = Factory.getSelectionAdapter().find(obj, IResource.class);
			if (res != null) {
				IContainer container = null;
				if (res instanceof IContainer)
					container = (IContainer) res;
				else
					container = res.getParent();
				if (container != null) {
					containerText.setText(container.getFullPath().toString());
				}
			}
			CMGroup gp = Factory.getSelectionAdapter().find(obj, CMGroup.class);
			if (gp != null) {
				String path = getServices().getFolderFullPathOfGroup(gp);
				containerText.setText(CoreUtils.cleanString(path));
			}
		}
		moduleNameText.setText("New module name");
		modulePackageNameText.setText("new_module_name");
	}

	/**
	 * Uses the standard container selection dialog to choose the new value for the
	 * container field.
	 */

	private void handleBrowse() {
		ContainerSelectionDialog dialog = new ContainerSelectionDialog(getShell(),
				ResourcesPlugin.getWorkspace().getRoot(), false, "Select new module container");
		if (dialog.open() == ContainerSelectionDialog.OK) {
			Object[] result = dialog.getResult();
			if (result.length == 1) {
				containerText.setText(((Path) result[0]).toString());
			}
		}
	}

	/**
	 * Ensures that both text fields are set.
	 */

	private void dialogChanged() {

		String containerName = getContainerName();
		String fileName = geModuleName();
		String packageName = getPackageName();

		if (CoreUtils.isNotNullOrEmpty(containerName) == false) {
			updateStatus("Folder container must be specified");
			return;
		}

		if (CoreUtils.isNotNullOrEmpty(dataSourceUuid) == false) {
			updateStatus("A data source must be specified");
			return;
		}
		if (fileName.length() == 0) {
			updateStatus("Name must be specified");
			return;
		}
		if (ValidatorUtils.isValidModuleName(fileName) == false) {
			updateStatus("Name must be valid");
			return;
		}
		if (packageName.length() == 0) {
			updateStatus("Package name must be specified");
			return;
		}
		if (ValidatorUtils.isValidPackageName(packageName) == false) {
			updateStatus("Package name must be valid");
			return;
		}

		Object container = null;

		try {
			container = getServices().getModulesManager().getFolderFromFullPath(dataSourceUuid,
					new String[] { containerName, packageName });

		} catch (Exception e) {
			updateStatus(e.getMessage());
			return;
		}

		Object parentFolder = getServices().getModulesManager().getParentFolder(container);
		if (parentFolder != null && Factory.getCMServices().isModulePackageFreeForEclipseWorkspace(null, dataSourceUuid,
				parentFolder, packageName) == false) {
			updateStatus("Package name already used");
			return;
		}

		updateStatus(null);
	}

	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	public String getContainerName() {
		return containerText.getText();
	}

	public String geModuleName() {
		return moduleNameText.getText();
	}

	public String getPackageName() {
		return modulePackageNameText.getText();
	}

	public CMService getServices() {
		return Factory.getCMServices();
	}

	public String getDataSourceUuid() {
		return dataSourceUuid;
	}

	public void setDataSourceUuid(String dataSourceUuid) {
		this.dataSourceUuid = dataSourceUuid;
	}

}