package org.crealogix.transaction.client;

import java.util.UUID;

import org.crealogix.infraestructure.interfaces.dispatcher.CommandContext;
import org.crealogix.infraestructure.interfaces.events.EventPublisher;
import org.crealogix.infraestructure.interfaces.repository.Repository;
import org.crealogix.transaction.data.aggregates.Client;

public class ClientCommandContext extends CommandContext<Client, UUID> {

    public ClientCommandContext(final Repository<Client, UUID> repository,
                                 final EventPublisher<UUID> eventPublisher) {
        super(repository, eventPublisher);
    }
}
