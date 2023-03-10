package com.weelgo.eclipse.plugin.chainmapping.editor;

import org.eclipse.gef.tools.ConnectionCreationTool;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.events.KeyEvent;

import com.weelgo.eclipse.plugin.KeyHelper;
import com.weelgo.eclipse.plugin.chainmapping.editor.actions.CreateNeedAction;
import com.weelgo.eclipse.plugin.chainmapping.editor.actions.CreateTaskAction;

public class LinkTool extends ConnectionCreationTool {

	private ChainMappingEditor chainMappingEditor;

	public LinkTool(ChainMappingEditor chainMappingEditor) {
		this.chainMappingEditor = chainMappingEditor;
	}

	@Override
	protected boolean handleKeyDown(KeyEvent event) {
		if (KeyHelper.isKey(event, ChainMappingEditor.SELECTION_TOOL_KEY) || KeyHelper.isESCAPE(event)) {
			getDomain().loadDefaultTool();
			return true;
		}
		if (KeyHelper.isKey(event, ChainMappingEditor.CREATE_TASK_KEY)) {
			IAction t = chainMappingEditor.getActionRegistry().getAction(CreateTaskAction.CREATE_TASK);
			t.run();
			return true;
		}
		if (KeyHelper.isKey(event, ChainMappingEditor.CREATE_NEED_KEY)) {
			IAction t = chainMappingEditor.getActionRegistry().getAction(CreateNeedAction.CREATE_NEED);
			t.run();
			return true;
		}
		return super.handleKeyDown(event);
	}

	@Override
	protected boolean handleButtonDown(int button) {
		if (button == 3) {
			handleFinished();
		}
		return super.handleButtonDown(button);
	}

}
