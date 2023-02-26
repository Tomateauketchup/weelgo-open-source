package com.weelgo.eclipse.plugin.chainmapping.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.CreateRequest;

import com.weelgo.chainmapping.core.CMNode;
import com.weelgo.eclipse.plugin.Factory;
import com.weelgo.eclipse.plugin.job.CMMoveNodesJob;

public class AppEditLayoutPolicy extends XYLayoutEditPolicy {

	@Override
	protected Command createChangeConstraintCommand(EditPart child, Object constraint) {

		if (child instanceof NodeEditPart) {

			return Factory.createCommand(() -> {

				Rectangle rect = (Rectangle) constraint;

				// On calcule le dépcalement
				NodeEditPart nodePart = (NodeEditPart) child;
				CMNode node = nodePart.getModelNode();
				
				//We must reajust the position because of positions of name
				if(child instanceof NodeEditPart np)
				{
					np.recalculateMousePosition(rect);
				}

				int deltaX = rect.x - node.getPositionX();
				int deltaY = rect.y - node.getPositionY();

				List<CMNode> arl = new ArrayList<CMNode>();
				CMNode newNode = new CMNode();
				newNode.setUuid(node.getUuid());
				newNode.setModuleUniqueIdentifier(node.getModuleUniqueIdentifier());
				newNode.setPositionX(node.getPositionX() + deltaX);
				newNode.setPositionY(node.getPositionY() + deltaY);
				arl.add(newNode);
				
				

//				List<CMNode> selectedNodes = Factory.getCurrentSelectionService().findList(CMNode.class);
//				if(selectedNodes!=null)
//				{
//					for (CMNode n : selectedNodes) {
//						CMNode newNode = new CMNode();
//						newNode.setUuid(n.getUuid());
//						newNode.setModuleUniqueIdentifier(n.getModuleUniqueIdentifier());
//						newNode.setPositionX(n.getPositionX()+deltaX);
//						newNode.setPositionY(n.getPositionY()+deltaY);
//						arl.add(newNode);
//					}
//				}

				CMMoveNodesJob j = CMMoveNodesJob.CREATE();
				j.setSelectedObject(arl);
				j.doSchedule();

			});

		}
		return null;
	}

	@Override
	protected Command getCreateCommand(CreateRequest arg0) {
		return null;
	}

}
