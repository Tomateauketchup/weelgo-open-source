package com.weelgo.eclipse.plugin;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.services.events.IEventBroker;

@Creatable
@Singleton
public class EventBroker{
	
	@Inject
	private IEventBroker eventBroker;
	
	public boolean sentEvent(String topic, Object data) {
		return eventBroker.post(topic, data);
	}

}
