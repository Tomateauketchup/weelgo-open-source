package com.weelgo.eclipse.plugin.job;

import org.eclipse.core.runtime.jobs.ISchedulingRule;

public class CMJobRule implements ISchedulingRule {

	private static long index = 0;

	private long indexAtCreation = 0;
	private String moduleUniqueIdentifier;

	public CMJobRule(String moduleUniqueIdentifier) {
		index++;
		indexAtCreation = index;
		this.moduleUniqueIdentifier = moduleUniqueIdentifier;
	}

	@Override
	public boolean isConflicting(ISchedulingRule rule) {
		if (rule == this)
			return true;
		if (rule instanceof CMJobRule) {
			CMJobRule r = (CMJobRule) rule;

			// For now we don't compare the module identifier. This is the safer mode but
			// can be annoying to have to wait the operation from another module to do the
			// operation on the module we are working on

			return r.indexAtCreation > indexAtCreation;
		}
		return rule == this;
	}

	@Override
	public boolean contains(ISchedulingRule rule) {
		return rule == this;
	}

	public long getIndexAtCreation() {
		return indexAtCreation;
	}

	public void setIndexAtCreation(long indexAtCreation) {
		this.indexAtCreation = indexAtCreation;
	}

	public String getModuleUniqueIdentifier() {
		return moduleUniqueIdentifier;
	}

	public void setModuleUniqueIdentifier(String moduleUniqueIdentifier) {
		this.moduleUniqueIdentifier = moduleUniqueIdentifier;
	}

};