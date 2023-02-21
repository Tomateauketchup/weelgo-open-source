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
import com.weelgo.chainmapping.core.CMModulesManager;
import com.weelgo.core.CoreUtils;
import com.weelgo.core.exceptions.ExceptionsUtils;
import com.weelgo.eclipse.plugin.CMService;
import com.weelgo.eclipse.plugin.EventBroker;
import com.weelgo.eclipse.plugin.Factory;
import com.weelgo.eclipse.plugin.ImagesFactory;
import com.weelgo.eclipse.plugin.ProgressMonitorAdapter;
import com.weelgo.eclipse.plugin.undoredo.UndoRedoService;

public abstract class CMJob extends Job {

	private static Logger logger = LoggerFactory.getLogger(CMJob.class);
	@Inject
	private CMService services;
	@Inject
	private EventBroker eventBroker;
	@Inject
	private UndoRedoService undoRedoService;
	private String moduleUniqueIdentifier;
	private String beginTaskMessage = "Starting Weelgo job ...";
	private String undoRedoTargetName;

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

			if (isUndoRedoJob()) {
				undoRedoService.saveModel(getModuleUniqueIdentifier(), getUndoRedoLabel(), getUndoRedoIcon(),
						getUndoRedoTargetName());
				if (isMarkAsNotDirty()) {
					getServices().getModulesManager().markModelAsNotDirty(getModuleUniqueIdentifier());
				}
			} else if (isUndoRedoAllModulesJob()) {
				undoRedoService.saveAllModel(getUndoRedoLabel(), getUndoRedoIcon());
				if (isMarkAsNotDirty()) {
					getServices().getModulesManager().markAllModelAsNotDirty();
				}
			}
		} catch (Exception e) {

			if (isUndoRedoJob()) {
				undoRedoService.restoreModel(getModuleUniqueIdentifier());
			} else if (isUndoRedoAllModulesJob()) {
				undoRedoService.restoreAllModel();
			}
			ExceptionsUtils.ManageException(e, logger);
		}

		return Status.OK_STATUS;
	}

	public void createServices() {
		services = Factory.getCMServices();
		eventBroker = Factory.getEventBroker();
	}

	public boolean isUndoRedoJob() {
		return false;
	}

	public boolean isUndoRedoAllModulesJob() {
		return false;
	}

	public boolean isMarkAsNotDirty() {
		return false;
	}

	public String getUndoRedoLabel() {
		return "Job";
	}

	public String getUndoRedoIcon() {
		return ImagesFactory.MODIFY_ICON;
	}

	public void addRule(ISchedulingRule... rules) {
		ArrayList<ISchedulingRule> arl = new ArrayList<>();
		ISchedulingRule currentRule = getRule();
		if (currentRule != null) {
			arl.add(currentRule);
		}
		CoreUtils.putArrayIntoList(rules, arl);

		ISchedulingRule combinedRule = null;
		for (ISchedulingRule rule : arl) {
			if (rule != null) {
				combinedRule = MultiRule.combine(rule, combinedRule);
			}
		}
		setRule(combinedRule);

	}

	public String getUndoRedoTargetName() {
		return undoRedoTargetName;
	}

	public void setUndoRedoTargetName(String undoRedoTargetName) {
		this.undoRedoTargetName = undoRedoTargetName;
	}

	public void doSchedule() {

		schedule();
	}

	public CMModuleService getModuleService(Object o) {
		return getServices().findModuleService(o);
	}

	public boolean sentEvent(String topic) {
		return getEventBroker().sentEvent(topic, null);
	}

	public boolean sentEvent(String topic, Object data) {
		return getEventBroker().sentEvent(topic, data);
	}

	public String getBeginTaskMessage() {
		return beginTaskMessage;
	}

	public void setBeginTaskMessage(String beginTaskMessage) {
		this.beginTaskMessage = beginTaskMessage;
	}

	public abstract void doRun(com.weelgo.core.IProgressMonitor monitor);

	public EventBroker getEventBroker() {
		return eventBroker;
	}

	public void setEventBroker(EventBroker eventBroker) {
		this.eventBroker = eventBroker;
	}

	public CMService getServices() {
		return services;
	}

	public void setServices(CMService services) {
		this.services = services;
	}

	public CMModulesManager getModulesManager() {
		return getServices().getModulesManager();
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
