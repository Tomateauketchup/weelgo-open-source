package com.weelgo.eclipse.plugin.chainmapping.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;

import com.weelgo.chainmapping.core.CMModuleService;
import com.weelgo.core.CoreUtils;

public class CMEditorEditPart extends CMGenericEditPart {

	@Override
	protected IFigure createFigure() {
		FreeformLayer layer = new FreeformLayer();
		layer.setLayoutManager(new FreeformLayout());
		return layer;
	}	

	@Override
	protected List getModelChildren() {
		List<Object> arl = new ArrayList<>();
		CMModuleService ser = getModuleServiceModel();
		if (ser != null) {
			CoreUtils.putListIntoList(ser.getTasks(), arl);
		}
		return arl;
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new AppEditLayoutPolicy());
	}

	public CMModuleService getModuleServiceModel() {
		return (CMModuleService) getModel();
	}
}
