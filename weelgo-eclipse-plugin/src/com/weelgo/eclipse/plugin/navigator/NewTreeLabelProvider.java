package com.weelgo.eclipse.plugin.navigator;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.weelgo.chainmapping.core.navigator.NavNode;
import com.weelgo.eclipse.plugin.CMService;
import com.weelgo.eclipse.plugin.ImagesFactory;

@Creatable
public class NewTreeLabelProvider extends LabelProvider {

	@Inject
	private CMService cmServices;

	public CMService getCmServices() {
		return cmServices;
	}

	public void setCmServices(CMService cmServices) {
		this.cmServices = cmServices;
	}

	@Override
	public String getText(Object element) {

		if (element instanceof NavNode navNode) {

			String str = navNode.getName();
			if (navNode.isSubmodulesDirty()) {
				str = "+ " + str;
			}
			if (navNode.isDirty()) {
				str = "* " + str;
			}
			return str;
		}
		return super.getText(element);
	}

	@Override
	public Image getImage(Object element) {

		if (element instanceof NavNode navNode) {

			if (navNode.isFolder()) {
				return ImagesFactory.getIconImage(ImagesFactory.FOLDER_ICON);
			} else if (navNode.isGroup()) {
				return ImagesFactory.getIconImage(ImagesFactory.GROUP_ICON);
			} else if (navNode.isModule()) {
				return ImagesFactory.getIconImage(ImagesFactory.CHAIN_MAPPING_ICON);
			} else if (navNode.isSource()) {
				return ImagesFactory.getIconImage(ImagesFactory.SOURCE);
			} else if (navNode.isProject()) {
				return ImagesFactory.getIconImage(ImagesFactory.PROJECT_FOLDER);
			}

		}

		return super.getImage(element);
	}

}
