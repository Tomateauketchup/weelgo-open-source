package com.weelgo.eclipse.plugin;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class ImagesFactory {

	public static String TASK_ICON = "task.png";
	public static String CHAIN_MAPPING_ICON = "chain_mapping.png";
	public static String DELIVERABLE_ICON = "deliverable.gif";
	public static String FILE_ICON = "file.png";
	public static String FOLDER_ICON = "folder.gif";
	public static String GROUP_ICON = "package.gif";
	public static String GROUP_PROJECT = "project.gif";
	public static String SAVE_ICON = "save.png";
	public static String MODIFY_ICON = "modify.gif";
	
	private static Map<String, Image> imagesMap = new HashMap<String, Image>();

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
