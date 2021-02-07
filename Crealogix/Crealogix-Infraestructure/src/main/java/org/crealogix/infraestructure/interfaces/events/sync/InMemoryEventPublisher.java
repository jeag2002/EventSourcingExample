package org.crealogix.infraestructure.interfaces.events.sync;



import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import org.crealogix.infraestructure.interfaces.entities.actions.Event;
import org.crealogix.infraestructure.interfaces.events.EventConsumer;
import org.crealogix.infraestructure.interfaces.events.EventPublisher;
import org.crealogix.infraestructure.interfaces.exceptions.EventException;

public class InMemoryEventPublisher<U> implements EventPublisher<U> {

    private final Set<EventConsumer<U>> eventConsumers;
    private final ConcurrentMap<U, AtomicLong> currentVersions;

    public InMemoryEventPublisher() {
        eventConsumers = new HashSet<>();
        currentVersions = new ConcurrentHashMap<>();
    }

    public Set<EventConsumer<U>> getEventConsumers() {
        return Collections.unmodifiableSet(eventConsumers);
    }

    public void registerEventConsumer(final EventConsumer<U> eventConsumer) {
        eventConsumers.add(eventConsumer);
    }

    @Override
    public void tryPublish(final long expectedVersion, final List<Event<U>> events) {
        if (events.stream().map(Event<U>::getAggregateRootId).distinct().count() > 1) {
            throw new EventException("Los eventos afectan a más de una instancia de agregada");
        }

        final var aggregateRootId = events.get(0).getAggregateRootId();
        final var newVersion = expectedVersion + events.size();
        long currentVersion = casCurrentVersion(aggregateRootId, expectedVersion, newVersion);
        

        for (final Event<U> event : events) {
            notifyEventConsumers(++currentVersion, event);
        }
    }

    private long casCurrentVersion(final U id, long expectedVersion, long newVersion) {
        final var currentVersion = currentVersions.computeIfAbsent(id, i -> new AtomicLong(expectedVersion));
        return currentVersion.compareAndExchange(expectedVersion, newVersion);
    }

    private void notifyEventConsumers(final long version, final Event<U> event) {
        eventConsumers.forEach(c -> c.consume(version,  event));
    }
}
