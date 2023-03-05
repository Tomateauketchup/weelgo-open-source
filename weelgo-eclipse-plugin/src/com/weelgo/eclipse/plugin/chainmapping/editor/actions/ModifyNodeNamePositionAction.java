package com.weelgo.eclipse.plugin.chainmapping.editor.actions;

import java.util.List;

import org.eclipse.ui.IWorkbenchPart;

import com.weelgo.chainmapping.core.CMNode;
import com.weelgo.core.CoreUtils;
import com.weelgo.eclipse.plugin.job.CMModifyNodeNamePositionJob;

public class ModifyNodeNamePositionAction extends GenericSelectionAction {

	public static String MODIFY_NODES_NAME_POSITION_TOP = "modify_nodes_name_position_top";
	public static String MODIFY_NODES_NAME_POSITION_BOTTOM = "modify_nodes_name_position_bottom";
	public static String MODIFY_NODES_NAME_POSITION_LEFT = "modify_nodes_name_position_left";
	public static String MODIFY_NODES_NAME_POSITION_RIGHT = "modify_nodes_name_position_right";
	private String position;

	public ModifyNodeNamePositionAction(String position, IWorkbenchPart part) {
		super(part);
		setText("Modify node name position");
		setToolTipText("Modify node name position");
		this.position = position;
		if (CMNode.NAME_BOTTOM.equals(position)) {
			setId(MODIFY_NODES_NAME_POSITION_BOTTOM);
		} else if (CMNode.NAME_TOP.equals(position)) {
			setId(MODIFY_NODES_NAME_POSITION_TOP);
		} else if (CMNode.NAME_LEFT.equals(position)) {
			setId(MODIFY_NODES_NAME_POSITION_LEFT);
		} else {
			setId(MODIFY_NODES_NAME_POSITION_RIGHT);
		}
	}

	@Override
	protected void init() {
		super.init();
	}

	@Override
	public void run() {
		CMModifyNodeNamePositionJob j = CMModifyNodeNamePositionJob.CREATE();
		List<CMNode> nodes = CoreUtils.cloneList(findList(CMNode.class));
		if (nodes != null) {
			for (CMNode n : nodes) {
				if (n != null) {
					n.setNamePosition(position);
				}
			}
		}
		j.setSelectedObject(nodes);
		CMNode gp = find(CMNode.class);
		if (gp != null) {
			j.setModuleUniqueIdentifier(gp.getModuleUniqueIdentifier());
		}
		j.doSchedule();
	}

}
