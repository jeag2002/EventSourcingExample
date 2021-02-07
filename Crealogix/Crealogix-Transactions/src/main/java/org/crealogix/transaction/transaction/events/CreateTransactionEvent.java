package org.crealogix.transaction.transaction.events;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import org.crealogix.infraestructure.interfaces.entities.actions.Event;
import org.crealogix.transaction.data.aggregates.Client;
import org.crealogix.transaction.data.entities.Account;

import lombok.Value;

@Value
public class CreateTransactionEvent implements Event<UUID>  {
	
	UUID aggregateRootId;
	String aggregateRootIdStr;
	ArrayList<Client> clients;
	ArrayList<Account> accounts;
	float importTrans;
	boolean active;

}
