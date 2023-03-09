package com.weelgo.eclipse.plugin.job;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.inject.Inject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.MultiRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weelgo.chainmapping.core.CMModuleService;
import com.weelgo.chainmapping.core.CMModulesManager;
import com.weelgo.core.CoreUtils;
import com.weelgo.core.exceptions.ExceptionsUtils;
import com.weelgo.eclipse.plugin.CMEvents;
import com.weelgo.eclipse.plugin.CMService;
import com.weelgo.eclipse.plugin.EventBroker;
import com.weelgo.eclipse.plugin.Factory;
import com.weelgo.eclipse.plugin.ImagesFactory;
import com.weelgo.eclipse.plugin.ProgressMonitorAdapter;
import com.weelgo.eclipse.plugin.SelectionAdapter;
import com.weelgo.eclipse.plugin.ui.UiProperty;
import com.weelgo.eclipse.plugin.undoredo.UndoRedoService;

public abstract class CMJob extends Job {

	private static Logger logger = LoggerFactory.getLogger(CMJob.class);
	@Inject
	private CMService services;
	@Inject
	private EventBroker eventBroker;
	@Inject
	private UndoRedoService undoRedoService;
	@Inject
	private SelectionAdapter selectionAdapter;
	private String moduleUniqueIdentifier;
	private String beginTaskMessage = "Starting Weelgo job ...";
	private String undoRedoTargetName;
	private String eventTopic = null;
	private Object eventObject = null;
	private Object selectedObject;
	private boolean insideMultipleJobRun = false;
	private List<CMJobHandler> jobHandlers = new ArrayList<>();
	private int orderIndex = 0;

	public void createServices() {
		services = Factory.getCMServices();
		eventBroker = Factory.getEventBroker();
		undoRedoService = Factory.getUndoRedoService();
		selectionAdapter = Factory.getSelectionAdapter();
	}

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

	public boolean canExecuteJob() {
		return true;
	}
	
	public boolean makeEditorDirty()
	{
		return true;
	}

	@Override
	public IStatus run(IProgressMonitor monitor) {
		try {

			if (canExecuteJob()) {
				try {
					doRun(ProgressMonitorAdapter.beginTask(getBeginTaskMessage(), 100, monitor));

					if (isUndoRedoJob() && insideMultipleJobRun == false) {
						undoRedoService.saveModel(getModuleUniqueIdentifier(), getUndoRedoLabel(), getUndoRedoIcon(),
								getUndoRedoTargetName());
						if (isMarkAsNotDirty()) {
							getServices().getModulesManager().markModelAsNotDirty(getModuleUniqueIdentifier());
						}
					} else if (isUndoRedoAllModulesJob() && insideMultipleJobRun == false) {
						undoRedoService.saveAllModel(getUndoRedoLabel(), getUndoRedoIcon());
						if (isMarkAsNotDirty()) {
							getServices().getModulesManager().markAllModelAsNotDirty();
						}
					}

					// We send event after
					if (CoreUtils.isNotNullOrEmpty(eventTopic)) {
						eventBroker.sentEvent(eventTopic, eventObject);
					}

					executeSync(() -> {

						try {
							postJobUISync();
						} catch (Exception e) {
							ExceptionsUtils.ManageException(e, logger);
						}
					});

					sendStateToJobHandlers(CMJobHandler.STATUS_OK);
					
					if(makeEditorDirty())
					{
						sentEvent(CMEvents.CHECK_EDITOR_DIRTY);
					}

				} catch (Exception e) {

					if (isUndoRedoJob()) {
						undoRedoService.restoreModel(getModuleUniqueIdentifier());
					} else if (isUndoRedoAllModulesJob()) {
						undoRedoService.restoreAllModel();
					}

					sendStateToJobHandlers(CMJobHandler.STATUS_ERROR);
					ExceptionsUtils.ManageException(e, logger);
				}
			} else {
				sendStateToJobHandlers(CMJobHandler.STATUS_CANT_EXECUTE);
			}
		} finally {
			jobHandlers.clear();
		}

		return Status.OK_STATUS;
	}

	public void sendStateToJobHandlers(String state) {
		if (jobHandlers != null && state != null) {
			for (CMJobHandler cmJobHandler : jobHandlers) {
				if (cmJobHandler != null) {
					cmJobHandler.jobEnded(state);
				}
			}
		}
	}

	public void postJobUISync() {

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

	public void addJobHandler(CMJobHandler h) {
		if (h != null && jobHandlers != null) {
			jobHandlers.add(h);
		}
	}

	public <T> T getSelectedObject(Class<T> c) {
		return selectionAdapter.find(selectedObject, c);
	}

	public Object getSelectedObject() {
		return selectedObject;
	}

	public void setSelectedObject(Object selectedObject) {
		this.selectedObject = selectedObject;
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
		return selectionAdapter.findModuleService(o);
	}

	public void sentEvent(String topic) {
		eventTopic = topic;
	}

	public void sentEvent(String topic, Object data) {
		eventTopic = topic;
		eventObject = data;
	}

	public String getBeginTaskMessage() {
		return beginTaskMessage;
	}

	public void setBeginTaskMessage(String beginTaskMessage) {
		this.beginTaskMessage = beginTaskMessage;
	}

	public abstract void doRun(com.weelgo.core.IProgressMonitor monitor);

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

	public boolean isInsideMultipleJobRun() {
		return insideMultipleJobRun;
	}

	public void setInsideMultipleJobRun(boolean insideMultipleJobRun) {
		this.insideMultipleJobRun = insideMultipleJobRun;
	}

	public int getOrderIndex() {
		return orderIndex;
	}

	public void setOrderIndex(int orderIndex) {
		this.orderIndex = orderIndex;
	}

	public static void updateUI(Consumer<com.weelgo.core.IProgressMonitor> runnable) {
		updateUI("Update Weelgo UI", "Updating Weelgo UI ...", runnable);

	}

	public static void updateUI(String uiName, Consumer<com.weelgo.core.IProgressMonitor> runnable) {
		uiName = CoreUtils.cleanString(uiName);
		updateUI("Update " + uiName, "Updating " + uiName + " ...", runnable);

	}

	public void executeSync(Runnable c) {
		Factory.getUiSynchronize().syncExec(new Runnable() {

			@Override
			public void run() {
				c.run();
			}
		});
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

	public static void runMultipleJobs(List<CMJob> jobs, CMJobHandler generalHandler) {
		runOneJobs(jobs, 0, generalHandler, null, null, null);
	}

	public static void runMultipleJobs(List<CMJob> jobs, CMJobHandler generalHandler, boolean saveForUndoRedoManager,
			String undoRedoLabel, String undoRedoIcon) {

		Collections.sort(jobs, new Comparator<CMJob>() {

			@Override
			public int compare(CMJob o1, CMJob o2) {
				int i1 = 0;
				int i2 = 0;
				if (o1 != null && o2 != null) {
					i1 = o1.getOrderIndex();
					i2 = o2.getOrderIndex();
				}
				return Integer.compare(i1, i2);
			}
		});

		runOneJobs(jobs, 0, generalHandler, saveForUndoRedoManager ? new HashMap<String, String>() : null,
				undoRedoLabel, undoRedoIcon);
	}

	private static void runOneJobs(List<CMJob> jobs, int index, CMJobHandler generalHandler,
			Map<String, String> moduleUniqueIdentifiersForUndoRedo, String undoRedoLabel, String undoRedoIcon) {
		if (jobs != null && jobs.size() > index) {
			CMJob j = jobs.get(index);
			if (moduleUniqueIdentifiersForUndoRedo != null) {
				j.insideMultipleJobRun = true;
				if (CoreUtils.isNotNullOrEmpty(j.getModuleUniqueIdentifier())) {
					moduleUniqueIdentifiersForUndoRedo.put(j.getModuleUniqueIdentifier(),
							j.getModuleUniqueIdentifier());
				}
			}

			j.addJobHandler(new CMJobHandler() {

				@Override
				public void jobEnded(String status) {

					if (CMJobHandler.STATUS_OK.equals(status)) {
						runOneJobs(jobs, index + 1, generalHandler, moduleUniqueIdentifiersForUndoRedo, undoRedoLabel,
								undoRedoIcon);
					} else {
						if (generalHandler != null) {
							generalHandler.jobEnded(status);
						}
					}
				}
			});
			j.doSchedule();
		} else {
			if (generalHandler != null) {
				generalHandler.jobEnded(CMJobHandler.STATUS_OK);
				if (moduleUniqueIdentifiersForUndoRedo != null) {
					Collection<String> ids = moduleUniqueIdentifiersForUndoRedo.values();
					for (String s : ids) {
						Factory.getUndoRedoService().saveModel(s, undoRedoLabel, undoRedoIcon, null);
					}
				}
			}
		}
	}
}
