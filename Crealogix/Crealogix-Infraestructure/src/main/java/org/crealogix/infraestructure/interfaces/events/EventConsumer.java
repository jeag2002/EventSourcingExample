package org.crealogix.infraestructure.interfaces.events;

import org.crealogix.infraestructure.interfaces.entities.actions.Event;

@FunctionalInterface
public interface EventConsumer<U> {

    void consume(final long version, final Event<U> event);
}
