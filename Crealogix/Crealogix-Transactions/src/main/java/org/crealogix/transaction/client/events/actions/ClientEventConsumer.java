package org.crealogix.transaction.client.events.actions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

import org.crealogix.infraestructure.interfaces.entities.actions.Event;
import org.crealogix.infraestructure.interfaces.events.EventConsumer;
import org.crealogix.infraestructure.interfaces.repository.Repository;
import org.crealogix.transaction.client.events.RegisteredClientEvent;
import org.crealogix.transaction.data.aggregates.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


public class ClientEventConsumer implements EventConsumer<UUID>{

	
	private Repository<Client, UUID> repository;
	
	public ClientEventConsumer(final Repository<Client, UUID> repository) {
		this.repository = repository;
	}
	
	@Override
	public void consume(final long version, final Event<UUID> event) {
		
		if (event instanceof RegisteredClientEvent) {
			apply(version, (RegisteredClientEvent)event);
		}
		
	}
	
	
	private void apply(final long version, final RegisteredClientEvent event) {
		
		final var client = Client.builder()
				.id(event.getAggregateRootId())
				.idStr(event.getAggregateRootIdStr())
				.version(version)
				.enabled(event.isActive())
				.enabledMultiAccountPay(event.isMultiaccount())
				.accounts(event.getAccounts())
				.build();
		
		repository.save(client);
		
	}

	
	
	
}
