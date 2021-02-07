package org.crealogix.transaction.client.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.crealogix.infraestructure.interfaces.entities.actions.Command;
import org.crealogix.transaction.client.ClientCommandContext;
import org.crealogix.transaction.client.events.RegisteredClientEvent;
import org.crealogix.transaction.data.aggregates.Client;
import org.crealogix.transaction.data.entities.Account;
import org.crealogix.transaction.errors.CommandNotValidException;

import lombok.Value;

@Value
public class RegisterClientCommand implements Command<ClientCommandContext, Client, UUID> {
	
	UUID aggregateRootId;
    String aggregateRootIdStr;
    List<Account> accounts;


	@Override
	public void execute(ClientCommandContext context) {
		
	    
		if (context.getRepository().load(aggregateRootId).isPresent()) {
	        throw new CommandNotValidException("Client already exists!!");
	    }
	    
	    context.getEventPublisher().tryPublish(0L,new RegisteredClientEvent(aggregateRootId,aggregateRootIdStr,accounts,true,true));
	
		
	}

}
