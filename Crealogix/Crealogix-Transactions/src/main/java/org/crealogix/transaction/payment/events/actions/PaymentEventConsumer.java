package org.crealogix.transaction.payment.events.actions;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import org.crealogix.infraestructure.interfaces.dispatcher.sync.SynchronizedCommandDispatcher;
import org.crealogix.infraestructure.interfaces.entities.actions.Event;
import org.crealogix.infraestructure.interfaces.events.EventConsumer;
import org.crealogix.infraestructure.interfaces.events.sync.InMemoryEventPublisher;
import org.crealogix.infraestructure.interfaces.task.TaskScheduler;
import org.crealogix.transaction.client.events.RegisteredClientEvent;
import org.crealogix.transaction.data.aggregates.Transaction;
import org.crealogix.transaction.enums.TransactionState;
import org.crealogix.transaction.history.repository.RepositoryHistoryDB;
import org.crealogix.transaction.transaction.TransactionCommandContext;
import org.crealogix.transaction.transaction.events.ChangeTransactionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PaymentEventConsumer implements EventConsumer<Object>{
		
    private static final String TASK_TYPE = "timeout";
    private static final int DEFAULT_TIMEOUT = 2;

	private final Map<Class<?>, Consumer<Event<?>>> consumers;
	private final TransactionCommandContext context;
	private TaskScheduler taskScheduler;
	private SynchronizedCommandDispatcher<TransactionCommandContext, Transaction, UUID> transDispatcher;
	
	private final Logger logger = LoggerFactory.getLogger(PaymentEventConsumer.class);
	
	
	public PaymentEventConsumer(TransactionCommandContext transactionCommandContext, 
			SynchronizedCommandDispatcher<TransactionCommandContext, Transaction, UUID> transDispatcher,
			TaskScheduler scheduler) {
		this.taskScheduler = scheduler;
		this.transDispatcher = transDispatcher;
		this.consumers = buildConsumers();
		this.context = transactionCommandContext;
		((InMemoryEventPublisher<UUID>)this.context.getEventPublisher()).registerEventConsumer((EventConsumer)this);
	}
	
	
	private Map<Class<?>, Consumer<Event<?>>> buildConsumers() {
    	return 
    		Map.of(
    			ChangeTransactionEvent.class, e->apply((ChangeTransactionEvent)e)
    		);
    }
	
	
	private void apply(final ChangeTransactionEvent event) {
		
		logger.warn("[PAYMENT-EVENT ] process event state");
		
		
		if (event.getState().equals(TransactionState.CONFIRMED)) {
			applyConfirm(event);
		}else if (event.getState().equals(TransactionState.STOPPED_USR) || event.getState().equals(TransactionState.STOPPED_TO) || (event.getState().equals(TransactionState.STOPPED_GT))) {
			applyCancelWhileProcess(event);
		}else if (event.getState().equals(TransactionState.FINALIZED)) {
			applyFinalize(event);
		}
	}
	
	
	private void applyConfirm(ChangeTransactionEvent event) {
		
		logger.warn("[PAYMENT-EVENT ] process event state <" + event.getState() + ">. RUN TIMEOUT TASK");
		
		taskScheduler.scheduleTask(TASK_TYPE, event.getAggregateRootId(), 
				()->this.context.getEventPublisher().tryPublish(0L, new ChangeTransactionEvent(event.getAggregateRootId(), event.getIdStr(), TransactionState.STOPPED_TO)) , DEFAULT_TIMEOUT);
	}
	
	private void applyCancelWhileProcess(ChangeTransactionEvent event) {
		
		logger.warn("[PAYMENT-EVENT ] process event state <" + event.getState() + ">. STOP TIMEOUT TASK. SEND CANCEL FLAG");
		
		taskScheduler.cancelTask(TASK_TYPE, event.getAggregateRootId());
		this.context.getEventPublisher().tryPublish(0L, new ChangeTransactionEvent(event.getAggregateRootId(), event.getIdStr(), TransactionState.CANCELLED));
	}
	
	private void applyFinalize(ChangeTransactionEvent event) {
		
		logger.warn("[PAYMENT-EVENT ] process event state <"+ event.getState() + ">. END TIMEOUT PROCESS");
		
		taskScheduler.cancelTask(TASK_TYPE, event.getAggregateRootId());
	}
	
	
	@Override
	public void consume(long version, Event<Object> event) {
		
		final var consumer = consumers.get(event.getClass());
		if (consumer != null) {
			consumer.accept(event);
		}
		
	}

}
