package com.weelgo.eclipse.plugin.job;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weelgo.core.IProgressMonitor;
import com.weelgo.core.exceptions.ExceptionsUtils;
import com.weelgo.eclipse.plugin.Factory;
import com.weelgo.eclipse.plugin.chainmapping.editor.ChainMappingEditor;
import com.weelgo.eclipse.plugin.chainmapping.editor.ChainMappingEditorInput;

public class CMOpenChainMappingEditorJob extends CMJob {

	private static Logger logger = LoggerFactory.getLogger(CMOpenChainMappingEditorJob.class);

	public CMOpenChainMappingEditorJob() {
		super("Open chain-mapping editor", "Opening chain-mapping editor ...");
	}

	public static CMOpenChainMappingEditorJob CREATE() {
		return Factory.create(CMOpenChainMappingEditorJob.class);
	}

	@Override
	public boolean makeEditorDirty() {
		return false;
	}

	@Override
	public void postJobUISync() {

		try {

			ChainMappingEditorInput ei = ChainMappingEditorInput.CREATE();
			ei.setModuleUniqueIdentifier(getModuleUniqueIdentifier());
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			page.openEditor(ei, ChainMappingEditor.ID, false);
		} catch (Exception e) {
			ExceptionsUtils.ManageException(e, logger);
		}

	}

	@Override
	public void doRun(IProgressMonitor monitor) {

	}

}
