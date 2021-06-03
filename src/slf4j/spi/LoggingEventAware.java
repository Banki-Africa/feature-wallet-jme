package slf4j.spi;

import slf4j.event.LoggingEvent;

public interface LoggingEventAware {

	void log(LoggingEvent event);
	
}
