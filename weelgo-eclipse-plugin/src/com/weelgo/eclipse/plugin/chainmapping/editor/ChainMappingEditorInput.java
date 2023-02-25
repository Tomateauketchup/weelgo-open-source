package com.weelgo.eclipse.plugin.chainmapping.editor;

import javax.inject.Inject;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.weelgo.chainmapping.core.CMModuleService;
import com.weelgo.chainmapping.core.CMModulesManager;
import com.weelgo.core.IDisposableObject;
import com.weelgo.eclipse.plugin.CMService;
import com.weelgo.eclipse.plugin.Factory;
import com.weelgo.eclipse.plugin.ImagesFactory;

public class ChainMappingEditorInput implements IEditorInput,IDisposableObject {

	@Inject
	private CMService cmService;

	private String moduleUniqueIdentifier;


	public static ChainMappingEditorInput CREATE() {
		return Factory.create(ChainMappingEditorInput.class);
	}

	@Override
	public void disposeObject() {

	}

	@Override
	public <T> T getAdapter(Class<T> adapter) {
		return null;
	}

	@Override
	public boolean exists() {
		return getModuleService() != null;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof ChainMappingEditorInput))
			return false;
		return ((ChainMappingEditorInput) o).getModuleUniqueIdentifier().equals(getModuleUniqueIdentifier());
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return ImagesFactory.getIconsImageDescriptor(ImagesFactory.CHAIN_MAPPING_ICON);
	}

	@Override
	public String getName() {
		return getModuleService().getName();
	}

	public boolean isDirty()
	{
		return getModuleService().isDirty();
	}

	@Override
	public IPersistableElement getPersistable() {
		return null;
	}

	@Override
	public String getToolTipText() {
		return "Chain-mapping editor";
	}

	public String getModuleUniqueIdentifier() {
		return moduleUniqueIdentifier;
	}

	public void setModuleUniqueIdentifier(String moduleUniqueIdentifier) {
		this.moduleUniqueIdentifier = moduleUniqueIdentifier;
	}

	public CMModuleService getModuleService() {
		return getCmService().findModuleService(getModuleUniqueIdentifier());
	}

	public CMService getCmService() {
		return cmService;
	}

	public CMModulesManager getModulesManager() {
		return getCmService().getModulesManager();
	}

}
