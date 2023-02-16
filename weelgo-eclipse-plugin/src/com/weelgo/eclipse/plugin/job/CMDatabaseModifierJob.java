package com.weelgo.eclipse.plugin.job;

import org.eclipse.core.resources.ResourcesPlugin;

public abstract class CMDatabaseModifierJob extends CMJob {

	public CMDatabaseModifierJob() {
		super();
	}

	public CMDatabaseModifierJob(String name) {
		super(name);
	}

	public CMDatabaseModifierJob(String name, String beginTaskMessage) {
		super(name, beginTaskMessage);
	}

	@Override
	public void init() {
		super.init();
		addRule(ResourcesPlugin.getWorkspace().getRoot());
	}

}
