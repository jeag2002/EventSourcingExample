package org.crealogix.gateway.configuration;


import java.util.UUID;

import org.crealogix.infraestructure.interfaces.dispatcher.sync.SynchronizedCommandDispatcher;
import org.crealogix.infraestructure.interfaces.task.TaskScheduler;
import org.crealogix.transaction.data.aggregates.Transaction;
import org.crealogix.transaction.payment.events.actions.PaymentEventConsumer;
import org.crealogix.transaction.payment.events.actions.PaymentGatewayEventConsumer;
import org.crealogix.transaction.transaction.TransactionCommandContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentConfiguration {
	
	private final Logger logger = LoggerFactory.getLogger(PaymentConfiguration.class);
	
	@Bean(name="task")
	TaskScheduler createTaskSchedule() {
		logger.info("[TASKSCHEDULE-CONFIG] CREATE TASK SCHEDULER");
		return new TaskScheduler();
	}

	
	
	 @Bean(name="PaymentEventConsumer")
	 public PaymentEventConsumer createPaymentEventConsumer(
			 @Autowired @Qualifier("transactionCommandContext") TransactionCommandContext transactionCommandContext,
			 @Autowired @Qualifier("TransDispatcher") SynchronizedCommandDispatcher<TransactionCommandContext, Transaction, UUID> transDispatcher,
			 @Autowired @Qualifier("task") TaskScheduler scheduler) {
		 logger.info("[PAYMENT-CONFIG] CREATE EVENT PUBLISHER");
		 return new PaymentEventConsumer(transactionCommandContext, transDispatcher, scheduler);
	 }
	 
	 
	 @Bean(name="PaymentGatewayEventConsumer")
	 public PaymentGatewayEventConsumer createPaymentGatewayEventConsumer(
			 @Autowired @Qualifier("transactionCommandContext") TransactionCommandContext transactionCommandContext,
			 @Autowired @Qualifier("TransDispatcher") SynchronizedCommandDispatcher<TransactionCommandContext, Transaction, UUID> transDispatcher,
			 @Autowired @Qualifier("task") TaskScheduler scheduler) {
		 logger.info("[PAYMENTGATEWAY-CONFIG] CREATE EVENT PUBLISHER");
		 return new PaymentGatewayEventConsumer(transactionCommandContext, transDispatcher, scheduler);
	 }
	 

}
