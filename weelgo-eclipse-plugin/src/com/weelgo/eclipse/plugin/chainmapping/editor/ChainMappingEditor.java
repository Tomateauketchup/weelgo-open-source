package com.weelgo.eclipse.plugin.chainmapping.editor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weelgo.core.exceptions.ExceptionsUtils;

public class ChainMappingEditor extends GraphicalEditorWithFlyoutPalette {

	private static Logger logger = LoggerFactory.getLogger(ChainMappingEditor.class);

	public static final String ID = "com.weelgo.eclipse.plugin.chainmapping.editor.ChainMappingEditor";

	public ChainMappingEditor() {
		setEditDomain(new DefaultEditDomain(this));
	}

	@Override
	protected PaletteRoot getPaletteRoot() {
		return null;
	}

	@Override
	public void doSave(IProgressMonitor monitor) {

	}

	public static void openEditor(final String moduleUniqueIdentifier) {
		try {
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			page.openEditor(new ChainMappingEditorInput(moduleUniqueIdentifier), ChainMappingEditor.ID, false);
		} catch (Exception e) {
			ExceptionsUtils.ManageException(e, logger);
		}
	}

}
