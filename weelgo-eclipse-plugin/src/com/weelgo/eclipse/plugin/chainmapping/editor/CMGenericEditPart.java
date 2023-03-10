package com.weelgo.eclipse.plugin.chainmapping.editor;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.weelgo.chainmapping.core.CMModuleService;
import com.weelgo.chainmapping.core.IModuleUniqueIdentifierObject;
import com.weelgo.core.CoreUtils;
import com.weelgo.core.IDisposableObject;
import com.weelgo.core.IUuidObject;
import com.weelgo.eclipse.plugin.CMEvents;
import com.weelgo.eclipse.plugin.EventBroker;
import com.weelgo.eclipse.plugin.Factory;
import com.weelgo.eclipse.plugin.SelectionAdapter;

public abstract class CMGenericEditPart extends AbstractGraphicalEditPart implements IDisposableObject, EventHandler {

	@Inject
	EventBroker eventBroker;

	@Inject
	SelectionAdapter selectionAdapter;

	@Override
	public void disposeObject() {
		EventBroker.unsubscribe(eventBroker, this);
		eventBroker = null;
		Factory.dispose(getChildren());
	}

	@Override
	public void removeNotify() {
		super.removeNotify();
		disposeObject();
	}

	@PostConstruct
	public void postContruct() {
		eventBroker.subscribe(CMEvents.NODES_POSITION_CHANGED, this);
		eventBroker.subscribe(CMEvents.NODES_NAME_POSITION_CHANGED, this);
		eventBroker.subscribe(CMEvents.TASK_NAME_MODIFIED, this);
		eventBroker.subscribe(CMEvents.LINK_CREATED, this);

	}

	@Override
	public void handleEvent(Event event) {
		String topic = event.getTopic();
		Object object = event.getProperty(IEventBroker.DATA);

		eventRecieved(topic, object);
	}

	public void eventRecieved(String topic, Object object) {
		if (CMEvents.isTopicForMe(topic, CMEvents.NODES_POSITION_CHANGED, CMEvents.NODES_NAME_POSITION_CHANGED,
				CMEvents.TASK_NAME_MODIFIED, CMEvents.LINK_CREATED)) {
			if (isEventForMeUuidObject(object)) {
				refreshVisuals();
				refreshSourceConnections();
				refreshTargetConnections();
				doRefreshGroups();
				doCalculateAdnRefreshCorners();
			}
		}
	}

	public boolean isEventForMeModuleUniqueIdentifierObject(Object object) {
		if (object != null) {
			IModuleUniqueIdentifierObject o = selectionAdapter.findModuleUniqueIdentifierObject(object);
			if (o != null
					&& CoreUtils.isStrictlyEqualsString(getModuleUniqueIdentifier(), o.getModuleUniqueIdentifier())) {
				return true;
			}
		}
		return false;
	}

	public boolean isEventForMeUuidObject(Object object) {
		if (object != null) {
			IUuidObject o = selectionAdapter.findUuidObject(object, getUuid());
			if (o != null) {
				return true;
			}
		}
		return false;
	}

	public String getUuid() {

		IUuidObject o = selectionAdapter.find(this, IUuidObject.class);
		if (o != null) {
			return o.getUuid();
		}
		return "";
	}

	public String getModuleUniqueIdentifier() {
		return selectionAdapter.findModuleUniqueIdentifierObjectId(this);
	}

	public void refreshVisualsOnly() {
		refreshVisuals();
		List childs = getChildren();
		if (childs != null) {
			for (Object o : childs) {
				if (o != null && o instanceof CMGenericEditPart p) {
					p.refreshVisualsOnly();
					p.refreshSourceConnections();
					p.refreshTargetConnections();
				}
			}
		}
	}

	public void doRefreshGroups() {
		EditPart parent = getParent();
		if (parent instanceof CMEditorEditPart p) {
			p.refreshGroups();
		}
	}

	public void doCalculateAdnRefreshCorners() {
		EditPart parent = getParent();
		if (parent instanceof CMEditorEditPart p) {
			p.calculateAndrefreshCorners();
		}
	}

	public CMModuleService getModuleService() {
		if (this instanceof CMEditorEditPart p) {
			return p.getModuleServiceModel();
		} else {
			EditPart parent = getParent();
			if (parent instanceof CMEditorEditPart p) {
				return p.getModuleServiceModel();
			}
		}

		return null;
	}

}
