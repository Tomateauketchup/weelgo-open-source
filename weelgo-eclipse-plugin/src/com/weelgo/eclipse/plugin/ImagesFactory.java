package com.weelgo.eclipse.plugin;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class ImagesFactory {

	public static final int ICON_SIZE_IN_PX=16;
	public static final String TASK_ICON = "task.png";
	public static final String CHAIN_MAPPING_ICON = "chain_mapping.png";
	public static final String DELIVERABLE_ICON = "deliverable.gif";
	public static final String FILE_ICON = "file.png";
	public static final String FOLDER_ICON = "folder.gif";
	public static final String GROUP_ICON = "package.gif";
	public static final String GROUP_PROJECT = "project.gif";
	public static final String SAVE_ICON = "save.png";
	public static final String LOAD_ICON = "load.png";
	public static final String MODIFY_ICON = "modify.gif";
	public static final String ARROW_LEFT = "arrow_left.gif";
	public static final String UNDO = "undo_redo_view.gif";
	
	private static final Map<String, Image> imagesMap = new HashMap<String, Image>();

	public static Image getIconImage(String iconName) {
		Image image = imagesMap.get(iconName);
		if (image == null) {
			ImageDescriptor desc = getIconsImageDescriptor(iconName);
			image = desc.createImage();
			imagesMap.put(iconName, image);
		}
		return image;
	}

	public static ImageDescriptor getIconsImageDescriptor(String iconName) {
		String iconPath = "icons/";

		return AbstractUIPlugin.imageDescriptorFromPlugin(Factory.PLUGIN_ID, iconPath + iconName);

	}

}
