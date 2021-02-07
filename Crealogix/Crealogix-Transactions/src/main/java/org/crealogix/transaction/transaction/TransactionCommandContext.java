package org.crealogix.transaction.transaction;

import java.util.UUID;

import org.crealogix.infraestructure.interfaces.dispatcher.CommandContext;
import org.crealogix.infraestructure.interfaces.events.EventPublisher;
import org.crealogix.infraestructure.interfaces.repository.Repository;
import org.crealogix.transaction.data.aggregates.Client;
import org.crealogix.transaction.data.aggregates.Transaction;

public class TransactionCommandContext extends CommandContext<Transaction, UUID> {
	
	private Repository<Client, UUID> repClient;

	public TransactionCommandContext(
    							 final Repository<Client, UUID> repositoryClient,
    		                     final Repository<Transaction, UUID> repository,
                                 final EventPublisher<UUID> eventPublisher) {
        super(repository, eventPublisher);
        this.repClient = repositoryClient;
    }
	
	 public Repository<Client, UUID> getRepClient() {
			return repClient;
	 }

	 public void setRepClient(Repository<Client, UUID> repClient) {
			this.repClient = repClient;
	 }

	
}
