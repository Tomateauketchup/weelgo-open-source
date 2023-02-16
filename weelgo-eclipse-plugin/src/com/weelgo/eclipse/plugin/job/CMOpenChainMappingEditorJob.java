package com.weelgo.eclipse.plugin.job;

import com.weelgo.core.IProgressMonitor;
import com.weelgo.eclipse.plugin.Factory;

public class CMOpenChainMappingEditorJob extends CMJob {

	public static CMOpenChainMappingEditorJob CREATE() {
		return Factory.create(CMOpenChainMappingEditorJob.class);
	}

	public CMOpenChainMappingEditorJob() {
		super("Open chain-mapping editor", "Opening chain-mapping editor ...");
	}

	@Override
	public void doRun(IProgressMonitor monitor) {

	}

}
