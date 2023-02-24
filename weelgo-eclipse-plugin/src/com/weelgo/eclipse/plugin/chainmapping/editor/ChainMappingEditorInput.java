package com.weelgo.eclipse.plugin.chainmapping.editor;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.weelgo.eclipse.plugin.ImagesFactory;

public class ChainMappingEditorInput implements IEditorInput {

	private String moduleUniqueIdentifier;

	public ChainMappingEditorInput(String moduleUniqueIdentifier) {
		this.moduleUniqueIdentifier = moduleUniqueIdentifier;
	}

	@Override
	public <T> T getAdapter(Class<T> adapter) {
		return null;
	}

	@Override
	public boolean exists() {
		return true;
	}

	public boolean equals(Object o) {
		if (!(o instanceof ChainMappingEditorInput))
			return false;
		return ((ChainMappingEditorInput) o).getModuleUniqueIdentifier().equals(getModuleUniqueIdentifier());
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		// TODO Auto-generated method stub
		return ImagesFactory.getIconsImageDescriptor(ImagesFactory.CHAIN_MAPPING_ICON);
	}

	@Override
	public String getName() {
		return getModuleUniqueIdentifier();
	}

	@Override
	public IPersistableElement getPersistable() {
		return null;
	}

	@Override
	public String getToolTipText() {
		return null;
	}

	public String getModuleUniqueIdentifier() {
		return moduleUniqueIdentifier;
	}

	public void setModuleUniqueIdentifier(String moduleUniqueIdentifier) {
		this.moduleUniqueIdentifier = moduleUniqueIdentifier;
	}

}
