package org.crealogix.transaction.history.events.actions;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import org.crealogix.infraestructure.interfaces.entities.actions.Event;
import org.crealogix.infraestructure.interfaces.events.EventConsumer;
import org.crealogix.infraestructure.interfaces.repository.Repository;
import org.crealogix.transaction.client.events.RegisteredClientEvent;
import org.crealogix.transaction.data.entities.History;
import org.crealogix.transaction.enums.ActionState;
import org.crealogix.transaction.enums.TransactionState;
import org.crealogix.transaction.transaction.events.ChangeTransactionEvent;
import org.crealogix.transaction.transaction.events.CreateTransactionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HistoryEventConsumer implements EventConsumer<Object>{

    private final Repository<History, UUID> historyRepository;
    private final Map<Class<?>, Consumer<Event<?>>> consumers;
    
    private final Logger logger = LoggerFactory.getLogger(HistoryEventConsumer.class);
	
    
    public HistoryEventConsumer(final Repository<History, UUID> historyRepository) {
        this.historyRepository = historyRepository;
        this.consumers = buildConsumers();
    }

    private Map<Class<?>, Consumer<Event<?>>> buildConsumers() {
    	return 
    		Map.of(
    			RegisteredClientEvent.class, e->apply((RegisteredClientEvent)e),
    			CreateTransactionEvent.class, e->apply((CreateTransactionEvent)e),
    			ChangeTransactionEvent.class, e->apply((ChangeTransactionEvent)e)
    		);
    }
	
    
    
    private void apply(final ChangeTransactionEvent event) {
    	
    	logger.info("[HISTORYEVENTCONSUMER-EVENT] detected ChangeTransactionEvent flag EVENT");
    	
    	UUID uuid = UUID.randomUUID();
    	
    	ActionState state = ActionState.NO_ACTION;
    	
    	if (event.getState().equals(TransactionState.CONFIRMED)) {
    		state = state.CONFIRM_TRANSACTION;
    	}else if (event.getState().equals(TransactionState.STOPPED_GT)) {
    		state = state.STOP_GATEWAY_TRANSACTION;
    	}else if (event.getState().equals(TransactionState.STOPPED_TO)) {
    		state = state.STOP_TIMEOUT_TRANSACTION;
    	}else if (event.getState().equals(TransactionState.STOPPED_USR)) {
    		state = state.STOP_CANCEL_TRANSACTION;
    	}else if (event.getState().equals(TransactionState.CANCELLED)) {
    		state = state.CANCEL_TRANSACTION;
    	}else if (event.getState().equals(TransactionState.FINALIZED)) {
    		state = state.FINALIZE_TRANSACTION;
    	}
    	
    	History history = History
				  .builder()
				  .id(uuid)
				  .description(event.getIdStr())
				  .state(state)
				  .refId(event.getAggregateRootId())
				  .timestampHistory(LocalDateTime.now())
				  .build();
    	
    	historyRepository.save(history);
    }
    
    
    
    private void apply(final RegisteredClientEvent event) {
    	logger.info("[HISTORYEVENTCONSUMER-EVENT] detected RegisteredClientEvent EVENT");
    	
    	UUID uuid = UUID.randomUUID();
    	
    	History history = History
				  .builder()
				  .id(uuid)
				  .description(event.getAggregateRootIdStr())
				  .state(ActionState.CREATE_CLIENT)
				  .refId(event.getAggregateRootId())
				  .timestampHistory(LocalDateTime.now())
				  .build();
    	
    	historyRepository.save(history);
    }
	
    public void apply(final CreateTransactionEvent event) {
    	logger.info("[HISTORYEVENTCONSUMER-EVENT] detected CreateTransactionEvent EVENT");
    	
    	UUID uuid = UUID.randomUUID();
    	
    	History history = History
    					  .builder()
    					  .id(uuid)
    					  .description(event.getAggregateRootIdStr())
    					  .state(ActionState.CREATE_TRANSACTION)
    					  .refId(event.getAggregateRootId())
    					  .timestampHistory(LocalDateTime.now())
    					  .build();
    	
    	historyRepository.save(history);
    	
    }
    
	
	@Override
	public void consume(long version, Event<Object> event) {
		final var consumer = consumers.get(event.getClass());
		if (consumer != null) {
			consumer.accept(event);
		}
	}

}
