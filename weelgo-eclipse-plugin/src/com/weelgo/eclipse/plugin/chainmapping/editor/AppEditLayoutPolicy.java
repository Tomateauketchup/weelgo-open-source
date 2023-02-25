package com.weelgo.eclipse.plugin.chainmapping.editor;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;

import com.weelgo.eclipse.plugin.Factory;
import com.weelgo.eclipse.plugin.job.CMMoveTaskJob;

public class AppEditLayoutPolicy extends XYLayoutEditPolicy {

	@Override
	protected Command createChangeConstraintCommand(EditPart child, Object constraint) {

		if (child instanceof NodeEditPart) {

			return Factory.createCommand(() -> {

				Rectangle rect = (Rectangle) constraint;

				CMMoveTaskJob j = CMMoveTaskJob.CREATE();
				j.setSelectedObject(child);
				
				//We must reajust the position because of positions of name
				if(child instanceof NodeEditPart np)
				{
					np.recalculateMousePosition(rect);
				}
				
				j.setPositionX(rect.x);
				j.setPositionY(rect.y);
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
