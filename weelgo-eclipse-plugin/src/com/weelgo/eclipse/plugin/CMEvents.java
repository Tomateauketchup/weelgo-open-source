package com.weelgo.eclipse.plugin;

import com.weelgo.core.CoreUtils;

public class CMEvents {

	public static final String SELECTION_CHANGED = "CM/SELECTION_CHANGED";
	public static final String ALL_MODULE_LOADED = "CM/ALL_MODULE_LOADED";
	public static final String ALL_MODULE_SAVED = "CM/ALL_MODULE_SAVED";
	public static final String GROUP_CREATED = "CM/GROUP_CREATED";
	public static final String TASK_CREATED = "CM/TASK_CREATED";
	public static final String TASK_POSITION_CHANGED = "CM/TASK_POSITION_CHANGED";
	public static final String MODULE_CREATED = "CM/MODULE_CREATED";
	public static final String MODULE_LOADED = "CM/MODULE_LOADED";
	public static final String MODULE_SAVED = "CM/MODULE_SAVED";
	public static final String MODULE_UNDO_REDO_OPERATION_DONE = "CM/MODULE_UNDO_REDO_OPERATION_DONE";
	public static final String WORKSPACE_FOLDER_MODIFIED = "CM/WORKSPACE_FOLDER_MODIFIED";
	
	public static boolean isTopicForMe(String topic, String... ar) {
		if (ar != null) {
			for (String string : ar) {
				if (CoreUtils.isStrictlyEqualsString(string, topic)) {
					return true;
				}
			}
		}
		return false;
	}

}
