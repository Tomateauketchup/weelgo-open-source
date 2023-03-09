package com.weelgo.eclipse.plugin.chainmapping.editor.actions;

import org.eclipse.ui.IWorkbenchPart;

public class ActivateToolAction extends GenericSelectionAction {

	public static final String TOOL_LINK = "link_tool";
	public static final String TOOL_SELECTION = "selection_tool";

	private String tool;

	public ActivateToolAction(String tool, IWorkbenchPart part) {
		super(part);
		this.tool = tool;
		setId(tool);
	}

	@Override
	protected void init() {
		super.init();

	}

	@Override
	public void run() {
		getChainMappingEditor().selectTool(tool);
	}

}
