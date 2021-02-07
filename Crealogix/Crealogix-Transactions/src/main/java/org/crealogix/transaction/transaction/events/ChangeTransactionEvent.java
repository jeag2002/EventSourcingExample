package org.crealogix.transaction.transaction.events;

import java.util.UUID;

import org.crealogix.infraestructure.interfaces.entities.actions.Event;
import org.crealogix.transaction.enums.TransactionState;

import lombok.Value;

@Value
public class ChangeTransactionEvent implements Event<UUID> {
	UUID aggregateRootId;
	String idStr;
	TransactionState state;
}
