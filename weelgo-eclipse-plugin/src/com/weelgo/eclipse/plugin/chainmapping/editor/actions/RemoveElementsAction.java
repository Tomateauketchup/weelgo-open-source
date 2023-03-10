package com.weelgo.eclipse.plugin.chainmapping.editor.actions;

import org.eclipse.ui.IWorkbenchPart;

import com.weelgo.chainmapping.core.CMLink;
import com.weelgo.chainmapping.core.CMNode;
import com.weelgo.eclipse.plugin.ImagesFactory;
import com.weelgo.eclipse.plugin.job.CMRemoveElementsJob;

public class RemoveElementsAction extends GenericSelectionAction {

	public static String REMOVE_ELEMENTS = "remove_elements";

	public RemoveElementsAction(IWorkbenchPart part) {
		super(part);
		setImageDescriptor(ImagesFactory.getIconsImageDescriptor(ImagesFactory.REMOVE_ICON));
		setText("Remove");
		setToolTipText("Remove");
	}

	@Override
	protected void init() {
		super.init();
		setId(REMOVE_ELEMENTS);
	}

	@Override
	public void run() {
		CMRemoveElementsJob j = CMRemoveElementsJob.CREATE();
		j.setSelectedObject(findListMulti(CMNode.class, CMLink.class));
		CMNode gp = find(CMNode.class);
		if (gp != null) {
			j.setModuleUniqueIdentifier(gp.getModuleUniqueIdentifier());
		}
		j.doSchedule();
	}

}
