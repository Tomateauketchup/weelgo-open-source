package com.weelgo.eclipse.plugin.job;

import java.util.ArrayList;
import java.util.List;

import com.weelgo.chainmapping.core.CMLink;
import com.weelgo.chainmapping.core.CMModuleService;
import com.weelgo.chainmapping.core.CMNode;
import com.weelgo.core.CoreUtils;
import com.weelgo.core.IProgressMonitor;
import com.weelgo.core.IUuidObject;
import com.weelgo.eclipse.plugin.CMEvents;
import com.weelgo.eclipse.plugin.Factory;
import com.weelgo.eclipse.plugin.ImagesFactory;

public class CMRemoveElementsJob extends CMJob {

	public CMRemoveElementsJob() {
		super("Remove element(s)", "Removing element(s) ...");
	}

	@Override
	public String getUndoRedoLabel() {
		return "Remove element(s)";
	}

	@Override
	public boolean isUndoRedoJob() {
		return true;
	}

	@Override
	public String getUndoRedoIcon() {
		return ImagesFactory.REMOVE_ICON;
	}

	public static CMRemoveElementsJob CREATE() {
		return Factory.create(CMRemoveElementsJob.class);
	}

	@Override
	public boolean canExecuteJob() {
		List arl =  (List) getSelectedObject();		
		if (arl != null && arl.size() > 0) {
			return true;
		}

		return false;

	}

	@Override
	public void doRun(IProgressMonitor monitor) {

		List<IUuidObject> arl = (List<IUuidObject>) getSelectedObject();		

		if (arl != null && arl.size() > 0) {
			IUuidObject node = arl.get(0);
			if (node != null) {
				CMModuleService ser = getModuleService(node);
				if (ser != null) {
					setModuleUniqueIdentifier(ser.getModuleUniqueIdentifier());
					String[] uuids = CoreUtils.transformListToStringArray(arl);
					if (uuids != null && uuids.length > 0) {
						ser.removeElements(uuids);
						sentEvent(CMEvents.ELEMENTS_REMOVED, arl);
					}
				}
			}
		}
	}

}
