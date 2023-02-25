package com.weelgo.eclipse.plugin.chainmapping.editor;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

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
		eventBroker.subscribe(CMEvents.TASK_POSITION_CHANGED, this);
	}

	@Override
	public void handleEvent(Event event) {
		String topic = event.getTopic();
		Object object = event.getProperty(IEventBroker.DATA);

		eventRecieved(topic, object);
	}

	public void eventRecieved(String topic, Object object) {
		if (CMEvents.isTopicForMe(topic, CMEvents.TASK_POSITION_CHANGED)) {
			if (isEventForMeUuidObject(object)) {
				refreshVisuals();
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
			IUuidObject o = selectionAdapter.find(object, IUuidObject.class);
			if (o != null && CoreUtils.isStrictlyEqualsString(getUuid(), o.getUuid())) {
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
				}
			}
		}
	}

}
