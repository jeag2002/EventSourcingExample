package org.crealogix.transaction.client.events;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.crealogix.infraestructure.interfaces.entities.actions.Event;
import org.crealogix.transaction.data.entities.Account;

import lombok.Value;

@Value
public class RegisteredClientEvent implements Event<UUID> {
	UUID aggregateRootId;
	String aggregateRootIdStr;
	List<Account> accounts;
	boolean active;
	boolean multiaccount;
}
