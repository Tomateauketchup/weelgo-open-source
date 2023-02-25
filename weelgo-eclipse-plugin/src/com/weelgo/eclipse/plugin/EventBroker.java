package com.weelgo.eclipse.plugin;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.osgi.service.event.EventHandler;

@Creatable
@Singleton
public class EventBroker {

	@Inject
	private IEventBroker eventBroker;

	public boolean sentEvent(String topic, Object data) {
		return eventBroker.post(topic, data);
	}

	public boolean sentEvent(String topic) {
		return eventBroker.post(topic, null);
	}

	public boolean subscribe(String topic, EventHandler eventHandler) {
		return eventBroker.subscribe(topic, eventHandler);
	}

	public boolean unsubscribe(EventHandler eventHandler) {
		return eventBroker.unsubscribe(eventHandler);
	}
	
	public static boolean unsubscribe(EventBroker eb,EventHandler eventHandler) {
		if(eb!=null && eventHandler!=null)
		{
			return eb.unsubscribe(eventHandler);
		}
		return true;
	}

}
