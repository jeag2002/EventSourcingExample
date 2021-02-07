package org.crealogix.transaction.transaction.actions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.crealogix.infraestructure.interfaces.entities.actions.Command;
import org.crealogix.transaction.data.aggregates.Client;
import org.crealogix.transaction.data.aggregates.Transaction;
import org.crealogix.transaction.data.entities.Account;
import org.crealogix.transaction.errors.CommandNotValidException;
import org.crealogix.transaction.transaction.TransactionCommandContext;
import org.crealogix.transaction.transaction.events.CreateTransactionEvent;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.Value;

@Value
public class CreateTransactionCommand implements Command<TransactionCommandContext, Transaction, UUID> {
	
    UUID aggregateRootId;
    String aggregateRootIdStr;
    ArrayList<Client> clients;
    ArrayList<Account> accounts;
    float importTrans;
    
    
	
    @Override
	public void execute(TransactionCommandContext context) {
		
	    if (clients.size() < 2) {
	    	throw new CommandNotValidException("Not enough clients");
	    }
    	
	    if (accounts.size() < 2) {
	    	throw new CommandNotValidException("Not enough accounts");
	    }
	    
    	if (context.getRepository().load(aggregateRootId).isPresent()) {
	        throw new CommandNotValidException("Transaction already exists");
	    }
	    
    	
    	
    	
    	if (!clients.get(0).isEnabled()) {
    		throw new CommandNotValidException("Source Client Disabled");
    	}
    	
    	if (!clients.get(1).isEnabled()) {
    		throw new CommandNotValidException("Destination Client Disabled");
    	}
    	
    	
    	if (accounts.get(0).getImportValue() < importTrans) {
    		if ((clients.get(0).totalAccountClient() < importTrans) && (clients.get(0).isEnabledMultiAccountPay())) {
    			throw new CommandNotValidException("Not enough money in Source Client ");
    		}else {
    			throw new CommandNotValidException("Not enough money in a selected account of source client");
    		}
    	}
    	
    	
    	context.getEventPublisher().tryPublish(0L, new CreateTransactionEvent(aggregateRootId, aggregateRootIdStr, clients, accounts, importTrans, true));
    	
    	
    	
    	
		
	}

	
}
