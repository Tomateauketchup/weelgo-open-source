package com.weelgo.eclipse.plugin.job;

import java.util.ArrayList;
import java.util.function.Consumer;

import javax.inject.Inject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.MultiRule;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weelgo.chainmapping.core.CMModuleService;
import com.weelgo.core.CoreUtils;
import com.weelgo.core.exceptions.ExceptionsUtils;
import com.weelgo.eclipse.plugin.CMServices;
import com.weelgo.eclipse.plugin.Factory;
import com.weelgo.eclipse.plugin.ProgressMonitorAdapter;

public abstract class CMJob extends Job {

	private static Logger logger = LoggerFactory.getLogger(CMJob.class);
	@Inject
	private CMServices services;
	@Inject
	private IEventBroker eventBroker;
	private String moduleUniqueIdentifier;
	private String beginTaskMessage = "Starting Weelgo job ...";

	public CMJob() {
		super("Weelgo Job");
		init();
	}

	public CMJob(String name) {
		super(name);
		init();
	}

	public CMJob(String name, String beginTaskMessage) {
		super(name);
		init();
	}

	public void init() {
		setRule(new CMJobRule(""));
	}

	@Override
	public IStatus run(IProgressMonitor monitor) {

		try {
			doRun(ProgressMonitorAdapter.beginTask(getBeginTaskMessage(), 100, monitor));
		} catch (Exception e) {
			ExceptionsUtils.ManageException(e, logger);
		}

		return Status.OK_STATUS;
	}

	public void createServices() {
		services = Factory.getCMServices();
		eventBroker = Factory.getEventBroker();
	}

	public void addRule(ISchedulingRule... rules) {
		ArrayList<ISchedulingRule> arl = new ArrayList<>();
		ISchedulingRule currentRule = getRule();
		if (currentRule != null) {
			arl.add(currentRule);
		}
		CoreUtils.putIntoList(rules, arl);

		ISchedulingRule combinedRule = null;
		for (ISchedulingRule rule : arl) {
			if (rule != null) {
				combinedRule = MultiRule.combine(rule, combinedRule);
			}
		}
		setRule(combinedRule);
		
		
	}

	public void doSchedule()
	{
		schedule();
	}
	
	
	public CMModuleService getModuleService(Object o) {
		return getServices().getModuleService(o);
	}

	public boolean sentEvent(String topic) {
		return getEventBroker().post(topic, null);
	}

	public boolean sentEvent(String topic, Object data) {
		return getEventBroker().post(topic, data);
	}

	public String getBeginTaskMessage() {
		return beginTaskMessage;
	}

	public void setBeginTaskMessage(String beginTaskMessage) {
		this.beginTaskMessage = beginTaskMessage;
	}

	public abstract void doRun(com.weelgo.core.IProgressMonitor monitor);

	public IEventBroker getEventBroker() {
		return eventBroker;
	}

	public void setEventBroker(IEventBroker eventBroker) {
		this.eventBroker = eventBroker;
	}

	public CMServices getServices() {
		return services;
	}

	public void setServices(CMServices services) {
		this.services = services;
	}

	public String getModuleUniqueIdentifier() {
		return moduleUniqueIdentifier;
	}

	public void setModuleUniqueIdentifier(String moduleUniqueIdentifier) {
		this.moduleUniqueIdentifier = moduleUniqueIdentifier;
	}

	public static void updateUI(Consumer<com.weelgo.core.IProgressMonitor> runnable) {
		updateUI("Update Weelgo UI", "Updating Weelgo UI ...", runnable);

	}

	public static void updateUI(String uiName, Consumer<com.weelgo.core.IProgressMonitor> runnable) {
		uiName = CoreUtils.cleanString(uiName);
		updateUI("Update " + uiName, "Updating " + uiName + " ...", runnable);

	}

	public static void updateUI(String jobName, String jobMessageName,
			Consumer<com.weelgo.core.IProgressMonitor> runnable) {
		if (runnable != null) {
			CMUpdateUIJob j = CMUpdateUIJob.CREATE(jobName, jobMessageName, monitor -> {

				Factory.getUiSynchronize().syncExec(new Runnable() {

					@Override
					public void run() {
						runnable.accept(monitor);
					}
				});

			});
			j.doSchedule();
		}
	}
}
