package org.crealogix.infraestructure.interfaces.events;



import java.util.List;

import org.crealogix.infraestructure.interfaces.entities.actions.Event;


public interface EventPublisher<U> {
	
    void tryPublish(final long expectedVersion, List<Event<U>> events);

    default void tryPublish(final long expectedVersion, Event<U> event) {
        tryPublish(expectedVersion, List.of(event));
    }
}
