package org.crealogix.transaction.transaction.events.actions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.crealogix.infraestructure.interfaces.entities.actions.Event;
import org.crealogix.infraestructure.interfaces.events.EventConsumer;
import org.crealogix.infraestructure.interfaces.repository.Repository;
import org.crealogix.transaction.client.events.RegisteredClientEvent;
import org.crealogix.transaction.data.aggregates.Client;
import org.crealogix.transaction.data.aggregates.Transaction;
import org.crealogix.transaction.data.entities.Account;
import org.crealogix.transaction.enums.TransactionState;
import org.crealogix.transaction.transaction.events.ChangeTransactionEvent;
import org.crealogix.transaction.transaction.events.CreateTransactionEvent;

public class TransactionEventConsumer implements EventConsumer<UUID> {
	
	private Repository<Client, UUID> repositoryClient;
	private Repository<Transaction, UUID> repositoryTrans;
	

	public TransactionEventConsumer(
			final Repository<Client, UUID> repositoryClient,
			final Repository<Transaction, UUID> repositoryTrans) {
		
		this.repositoryClient = repositoryClient;
		this.repositoryTrans = repositoryTrans;
	}
	
	@Override
	public void consume(final long version, final Event<UUID> event) {
		
		if (event instanceof CreateTransactionEvent) {
			apply(version, (CreateTransactionEvent)event);
		}else if ((event instanceof ChangeTransactionEvent) && (((ChangeTransactionEvent)event).getState().equals(TransactionState.CONFIRMED))) {
			applyConfirm(version, (ChangeTransactionEvent)event);
		}else if ((event instanceof ChangeTransactionEvent) && (((ChangeTransactionEvent)event).getState().equals(TransactionState.CANCELLED))) {
			applyCancelled(version, (ChangeTransactionEvent)event);
		}else if ((event instanceof ChangeTransactionEvent) && (((ChangeTransactionEvent)event).getState().equals(TransactionState.FINALIZED))) {
			applyFinalized(version, (ChangeTransactionEvent)event);
		}
	}
	
	
	private void applyFinalized(final long version, final ChangeTransactionEvent event) {
		
		Transaction trans = repositoryTrans.load(event.getAggregateRootId()).get();
	
		
		List<Client> clients = trans.getAccounts_client();
		List<Account> accounts = trans.getAccounts_trans();
		
	
		
		Account destAccount = accounts.get(1).clone();
		destAccount.setImportValue(destAccount.getImportValue() + trans.getImportValue());	
		clients.get(1).getAccounts().remove(accounts.get(1));
		clients.get(1).getAccounts().add(destAccount);
		
		repositoryClient.update(clients.get(1));
		
		
		Account originAccount = accounts.get(0).clone();
		
		if (originAccount.getImportValue() >= trans.getImportValue()) {
			originAccount.setImportValue(originAccount.getImportValue()-trans.getImportValue());
			clients.get(0).getAccounts().remove(accounts.get(0));
			clients.get(0).getAccounts().add(originAccount);
			
		}else if (clients.get(0).isEnabledMultiAccountPay()) {
			
			float value = trans.getImportValue()-originAccount.getImportValue();
			
			originAccount.setImportValue(0);
			clients.get(0).getAccounts().remove(accounts.get(0));
			
			List<Account> accountsList = clients.get(0).getAccounts();
			int index = 0;
			boolean DONE = false;
			
			while (!DONE) {
				
				Account accountClone = accountsList.get(index).clone();
				value = accountClone.getImportValue() - value;
				
				if (value < 0) {
					accountClone.setImportValue(0);
					if (index < accountsList.size()) {index++;}
					value = value*(-1);
					DONE = false;
				}else {
					accountClone.setImportValue(value);
					DONE = true;
				}
				
				clients.get(0).getAccounts().remove(clients.get(0).getAccounts().get(index));
				clients.get(0).getAccounts().add(accountClone);
				
			}
			
			
			clients.get(0).getAccounts().add(originAccount);
		}
		
		repositoryClient.update(clients.get(0));
		//repositoryTrans.delete(event.getAggregateRootId());
		
		final var trans_1 = Transaction.builder()
				.id(event.getAggregateRootId())
				.idStr(trans.getIdStr())
				.version(version)
				.state(event.getState())
				.build();
		
		repositoryTrans.update(trans_1);
		
		
	}
	
	private void applyCancelled(final long version, final ChangeTransactionEvent event) {
		//repositoryTrans.delete(event.getAggregateRootId());
		
		final var trans = Transaction.builder()
				.id(event.getAggregateRootId())
				.idStr(event.getIdStr())
				.version(version)
				.state(event.getState())
				.build();
		
		repositoryTrans.update(trans);
		
		
	}
	
	private void applyConfirm(final long version, final ChangeTransactionEvent event) {
		
		final var trans = Transaction.builder()
				.id(event.getAggregateRootId())
				.idStr(event.getIdStr())
				.version(version)
				.state(event.getState())
				.build();
		
		repositoryTrans.update(trans);
	}
	

	private void apply(final long version, final CreateTransactionEvent event) {
		
		
		final var trans = Transaction.builder()
				.id(event.getAggregateRootId())
				.idStr(event.getAggregateRootIdStr())
				.state(TransactionState.CREATED)
				.version(version)
				.importValue(event.getImportTrans())
				.accounts_client(event.getClients())
				.accounts_trans(event.getAccounts())
				.build();
		
		repositoryTrans.save(trans);
		
	}
	
	

}
