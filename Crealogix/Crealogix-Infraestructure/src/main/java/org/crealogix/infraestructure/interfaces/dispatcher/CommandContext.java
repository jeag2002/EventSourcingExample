package org.crealogix.infraestructure.interfaces.dispatcher;


import org.crealogix.infraestructure.interfaces.entities.model.AggregateRoot;
import org.crealogix.infraestructure.interfaces.events.EventPublisher;
import org.crealogix.infraestructure.interfaces.repository.Repository;
import org.springframework.lang.NonNull;


public class CommandContext<T extends AggregateRoot<U>, U> {

    private final Repository<T, U> repository;
    private final EventPublisher<U> eventPublisher;

    public CommandContext(final Repository<T, U> repository, final EventPublisher<U> eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @NonNull
    public Repository<T, U> getRepository() {
        return repository;
    }

    @NonNull
    public EventPublisher<U> getEventPublisher() {
        return eventPublisher;
    }
}
