package com.weelgo.eclipse.plugin.chainmapping.editor;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.logging.log4j.core.Core;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.ui.services.IDisposable;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.weelgo.chainmapping.core.CMTask;
import com.weelgo.chainmapping.core.IModuleUniqueIdentifierObject;
import com.weelgo.core.CoreUtils;
import com.weelgo.core.IDisposableObject;
import com.weelgo.eclipse.plugin.CMEvents;
import com.weelgo.eclipse.plugin.EventBroker;
import com.weelgo.eclipse.plugin.Factory;
import com.weelgo.eclipse.plugin.SelectionAdapter;

public class EventReciever implements IDisposableObject, EventHandler {

	private ChainMappingEditor chainMappingEditor;

	@Inject
	EventBroker eventBroker;

	@Inject
	SelectionAdapter selectionAdapter;

	public static EventReciever CREATE() {
		return Factory.create(EventReciever.class);
	}

	@Override
	public void disposeObject() {

		EventBroker.unsubscribe(eventBroker, this);
		eventBroker = null;
		chainMappingEditor = null;
		selectionAdapter = null;
	}

	@PostConstruct
	public void postContruct() {
		eventBroker.subscribe(CMEvents.TASK_CREATED, this);
		eventBroker.subscribe(CMEvents.NEED_CREATED, this);
		eventBroker.subscribe(CMEvents.ELEMENTS_REMOVED, this);
		eventBroker.subscribe(CMEvents.MODULE_UNDO_REDO_OPERATION_DONE, this);
		eventBroker.subscribe(CMEvents.MODULE_SAVED, this);
		eventBroker.subscribe(CMEvents.ALL_MODULE_SAVED, this);
		eventBroker.subscribe(CMEvents.GROUP_CREATED, this);
		eventBroker.subscribe(CMEvents.NODES_POSITION_CHANGED, this);
		eventBroker.subscribe(CMEvents.NODES_NAME_POSITION_CHANGED, this);
		eventBroker.subscribe(CMEvents.CHECK_EDITOR_DIRTY, this);
		eventBroker.subscribe(CMEvents.TASK_NAME_MODIFIED, this);
		eventBroker.subscribe(CMEvents.ELEMENTS_MOVED_INTO_GROUP, this);

	}

	@Override
	public void handleEvent(Event event) {
		String topic = event.getTopic();
		Object object = event.getProperty(IEventBroker.DATA);

		if (CMEvents.isTopicForMe(topic, CMEvents.TASK_CREATED, CMEvents.NEED_CREATED, CMEvents.ELEMENTS_REMOVED,
				CMEvents.MODULE_UNDO_REDO_OPERATION_DONE, CMEvents.MODULE_SAVED, CMEvents.GROUP_CREATED,
				CMEvents.ALL_MODULE_SAVED, CMEvents.NODES_POSITION_CHANGED, CMEvents.NODES_NAME_POSITION_CHANGED,
				CMEvents.TASK_NAME_MODIFIED, CMEvents.CHECK_EDITOR_DIRTY, CMEvents.ELEMENTS_MOVED_INTO_GROUP)) {

			if (CMEvents.CHECK_EDITOR_DIRTY.equals(topic)) {
				getChainMappingEditor().checkDirty();
			}

			if (isForMe(object)) {

				boolean refreshForCreationOrRemove = CMEvents.isTopicForMe(topic, CMEvents.TASK_CREATED,
						CMEvents.NEED_CREATED, CMEvents.ELEMENTS_REMOVED, CMEvents.MODULE_UNDO_REDO_OPERATION_DONE,
						CMEvents.GROUP_CREATED, CMEvents.ELEMENTS_MOVED_INTO_GROUP);

				boolean refreshVisuals = CMEvents.isTopicForMe(topic, CMEvents.MODULE_UNDO_REDO_OPERATION_DONE,
						CMEvents.ELEMENTS_MOVED_INTO_GROUP);

				if (refreshForCreationOrRemove) {
					getChainMappingEditor().refreshForCreationOrRemove();
				}
				if (refreshVisuals) {
					getChainMappingEditor().refreshVisualsOnly();
				}
			}

		}
	}

	public boolean isForMeString(String id) {
		return CoreUtils.isStrictlyEqualsString(getModuleUniqueIdentifier(), id);
	}

	public boolean isForMe(Object o) {

		if (o != null) {
			if (o instanceof String) {
				return isForMeString((String) o);
			} else if (o instanceof IModuleUniqueIdentifierObject) {
				return isForMe(((IModuleUniqueIdentifierObject) o).getModuleUniqueIdentifier());
			} else {
				String id = selectionAdapter.findModuleUniqueIdentifierObjectId(o);
				return isForMeString(id);
			}
		}
		return false;
	}

	public String getModuleUniqueIdentifier() {
		return getChainMappingEditor().getModuleService().getModuleUniqueIdentifier();
	}

	public ChainMappingEditor getChainMappingEditor() {
		return chainMappingEditor;
	}

	public void setChainMappingEditor(ChainMappingEditor chainMappingEditor) {
		this.chainMappingEditor = chainMappingEditor;
	}

	// TODO créer une vue miniature dans laquelle tout le réseau est présent et à
	// partir duquel on peut sélectionner des zones

}
