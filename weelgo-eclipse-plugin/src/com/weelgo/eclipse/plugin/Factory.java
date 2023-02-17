package com.weelgo.eclipse.plugin;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.commands.MCommand;
import org.eclipse.e4.ui.model.application.commands.MCommandsFactory;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuFactory;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weelgo.core.CoreUtils;
import com.weelgo.core.StringUpdateListProcessor;
import com.weelgo.core.exceptions.ExceptionsUtils;
import com.weelgo.eclipse.plugin.job.CMLoadAllModulesJob;

@Creatable
@Singleton
public class Factory {

	private static Logger logger = LoggerFactory.getLogger(Factory.class);

	public static final String PLUGIN_ID = "com.weelgo.eclipse.plugin";

	public static final String COMMAND_CREATE_CM_GROUP_ID = "com.weelgo.eclipse.plugin.commands.CreateCMGroupCommand";

	private static Factory factory = null;
	@Inject
	private CMService cmServices;
	@Inject
	private IEventBroker eventBroker;
	@Inject
	private UISynchronize uiSynchronize;
	@Inject
	private CurrentSelectionService currentSelection;
	@Inject
	private UndoRedoService undoRedoService;

	private List<String> openedProjects = new ArrayList<>();

	public static IEclipseContext getActiveContext() {
		IEclipseContext context = getWorkbenchContext();
		return context == null ? null : context.getActiveLeaf();
	}

	public static IEclipseContext getWorkbenchContext() {
		return PlatformUI.getWorkbench().getService(IEclipseContext.class);
	}

	public static void loadFactory() {
		if (factory == null) {
			IEclipseContext localCtx = getActiveContext();
			ContextInjectionFactory.make(CurrentSelectionService.class, localCtx);

			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IResourceChangeListener listener = new IResourceChangeListener() {
				@Override
				public void resourceChanged(IResourceChangeEvent event) {
					if (event.getType() != IResourceChangeEvent.POST_CHANGE)
						return;

					if (Factory.getFactory().isWorkspaceModified()) {
						CMLoadAllModulesJob j = CMLoadAllModulesJob.CREATE();
						j.doSchedule();
					}
				}
			};
			workspace.addResourceChangeListener(listener);
			factory = ContextInjectionFactory.make(Factory.class, localCtx);

		}
	}

	public static Factory getFactory() {
		loadFactory();
		return factory;
	}

	public static Object getCurrentSelection() {
		loadFactory();
		return factory.currentSelection.getCurrentSelection();
	}

	public static CMService getCMServices() {
		loadFactory();
		return factory.cmServices;
	}

	public static UndoRedoService getUndoRedoService() {
		loadFactory();
		return factory.undoRedoService;
	}

	public static IEventBroker getEventBroker() {
		loadFactory();
		return factory.eventBroker;
	}

	public static UISynchronize getUiSynchronize() {
		loadFactory();
		return factory.uiSynchronize;

	}

	public static MDirectMenuItem createMDirectMenuItem(String label, String icon, Class handlerCLass) {
		MDirectMenuItem item = MMenuFactory.INSTANCE.createDirectMenuItem();
		item.setLabel(label);
		item.setContributionURI("bundleclass://com.weelgo.eclipse.plugin/" + handlerCLass.getCanonicalName());
		item.setIconURI("platform:/plugin/com.weelgo.eclipse.plugin/icons/" + icon);

		return item;
	}

	public static MHandledMenuItem createMHandledMenuItem(String label, String icon, String commandId) {
		MHandledMenuItem item = MMenuFactory.INSTANCE.createHandledMenuItem();
		MCommand cmd = MCommandsFactory.INSTANCE.createCommand();
		cmd.setElementId(commandId);
		item.setCommand(cmd);
		item.setLabel(label);
		item.setIconURI("platform:/plugin/com.weelgo.eclipse.plugin/icons/" + icon);
		return item;
	}

	public static <T> T create(Class<T> valueType, IEclipseContext eclipseContext) {
		return ContextInjectionFactory.make(valueType, eclipseContext);
	}

	public static <T> T create(Class<T> valueType) {
		return ContextInjectionFactory.make(valueType, getActiveContext());
	}

	public static List<String> findOpenedProjects() {
		try {
			List<String> projectsLst = new ArrayList<>();
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			IResource[] childs = root.members();
			if (childs != null) {
				for (IResource r : childs) {
					if (r != null && r instanceof IProject && ((IProject) r).isOpen()) {
						String id = r.getFullPath().toString();
						if (CoreUtils.isNotNullOrEmpty(id)) {
							projectsLst.add(id);
						}
					}
				}
			}
			return projectsLst;
		} catch (Exception e) {
			ExceptionsUtils.logException(e, logger);
		}
		return null;
	}

	public boolean isWorkspaceModified() {

		// TODO améliorer la détection de ressource : quand un nouveau projet arrive ou
		// est supprimé, il faut uniquement loader pour ce projet et pas loader les
		// autres car il est possible que le modèle soit modifié
		List<String> oldList = openedProjects;
		List<String> newList = findOpenedProjects();
		openedProjects = newList;
		StringUpdateListProcessor updator = new StringUpdateListProcessor(oldList, newList) {

			@Override
			public String generateUUid() {
				return null;
			}
		};
		updator.compileList();

		return updator.hasChanged();
	}

}
