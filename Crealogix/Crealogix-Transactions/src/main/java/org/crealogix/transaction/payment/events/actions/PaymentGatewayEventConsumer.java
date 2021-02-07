package org.crealogix.transaction.payment.events.actions;

import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;

import org.crealogix.infraestructure.interfaces.dispatcher.sync.SynchronizedCommandDispatcher;
import org.crealogix.infraestructure.interfaces.entities.actions.Event;
import org.crealogix.infraestructure.interfaces.events.EventConsumer;
import org.crealogix.infraestructure.interfaces.events.sync.InMemoryEventPublisher;
import org.crealogix.infraestructure.interfaces.task.TaskScheduler;
import org.crealogix.transaction.data.aggregates.Transaction;
import org.crealogix.transaction.enums.TransactionState;
import org.crealogix.transaction.transaction.TransactionCommandContext;
import org.crealogix.transaction.transaction.events.ChangeTransactionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PaymentGatewayEventConsumer implements EventConsumer<Object>{
	
    private static final String TASK_TYPE = "gateway";
    private static final int DEFAULT_TIMEOUT = 1;

	private final Map<Class<?>, Consumer<Event<?>>> consumers;
	private final TransactionCommandContext context;
	private TaskScheduler taskScheduler;
	private SynchronizedCommandDispatcher<TransactionCommandContext, Transaction, UUID> transDispatcher;
	
	private Random rand;

	private final Logger logger = LoggerFactory.getLogger(PaymentGatewayEventConsumer.class);
	
	public PaymentGatewayEventConsumer(
			TransactionCommandContext transactionCommandContext, 
			SynchronizedCommandDispatcher<TransactionCommandContext, Transaction, UUID> transDispatcher,
			TaskScheduler scheduler) {
		
		this.rand = new Random();
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
		
		if (event.getState().equals(TransactionState.CONFIRMED)) {
			applyConfirm(event);
		}
	}
	
	private void applyConfirm(ChangeTransactionEvent event) {
		
		logger.warn("[PAYMENTGATEWAY-EVENT ] PROCESS EVENT <"+event.getState()+"> LAUNCH EXTERNAL PROCESS");
		
		taskScheduler.scheduleTask(TASK_TYPE, event.getAggregateRootId(), 
				()->generateRandomFlag(event) , DEFAULT_TIMEOUT);
	}
	
	
	private void generateRandomFlag(ChangeTransactionEvent event) {
		int flag = rand.nextInt(2);
		if (flag == 0) {
			logger.warn("[PAYMENTGATEWAY-EVENT ] PROCESS END EXTERNAL PROCESS SET EVENT <"+TransactionState.STOPPED_GT+">");
			this.context.getEventPublisher().tryPublish(0L, new ChangeTransactionEvent(event.getAggregateRootId(), event.getIdStr() ,TransactionState.STOPPED_GT));
		}
		else {
			logger.warn("[PAYMENTGATEWAY-EVENT ] PROCESS END EXTERNAL PROCESS SET EVENT <"+TransactionState.FINALIZED+">");
			this.context.getEventPublisher().tryPublish(0L, new ChangeTransactionEvent(event.getAggregateRootId(), event.getIdStr() ,TransactionState.FINALIZED));
		}
	}
	

	
	@Override
	public void consume(long version, Event<Object> event) {
		
		final var consumer = consumers.get(event.getClass());
		if (consumer != null) {
			consumer.accept(event);
		}
	}

}
