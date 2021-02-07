package org.crealogix.transaction.transaction.actions;

import java.util.UUID;

import org.crealogix.infraestructure.interfaces.entities.actions.Command;
import org.crealogix.transaction.data.aggregates.Transaction;
import org.crealogix.transaction.enums.TransactionState;
import org.crealogix.transaction.errors.CommandNotValidException;
import org.crealogix.transaction.transaction.TransactionCommandContext;
import org.crealogix.transaction.transaction.events.ChangeTransactionEvent;

import lombok.Value;

@Value
public class ChangeTransactionCommand implements Command<TransactionCommandContext, Transaction, UUID>{

	UUID aggregateRootId;
	TransactionState state;
	
    @Override
	public void execute(TransactionCommandContext context) {
    	
    	Transaction trans = context.getRepository().load(aggregateRootId).orElseThrow();
    	
    	if (state.equals(TransactionState.NO_ACTION)) {
    		throw new CommandNotValidException("Unexpected new state");
    	}
    	
    	if (!trans.getState().equals(TransactionState.CREATED) && (state.equals(TransactionState.CONFIRMED)) ) {
    		throw new CommandNotValidException("No valid state change");
    	}
    	
    	if (!trans.getState().equals(TransactionState.CREATED) && state.equals(TransactionState.CANCELLED)) {
    		throw new CommandNotValidException("Not valid state change");
    	}
    	
    	if (!trans.getState().equals(TransactionState.CONFIRMED) && state.equals(TransactionState.STOPPED_USR)) {
    		throw new CommandNotValidException("Not valid state change");
    	}
    	
    	if (state.equals(TransactionState.STOPPED_GT) || state.equals(TransactionState.STOPPED_TO) || state.equals(TransactionState.CREATED) || state.equals(TransactionState.FINALIZED)) {
    		throw new CommandNotValidException("No valid new state");
    	}
    	
    	context.getEventPublisher().tryPublish(1L, new ChangeTransactionEvent(aggregateRootId,trans.getIdStr(),state));
    }
	
}
