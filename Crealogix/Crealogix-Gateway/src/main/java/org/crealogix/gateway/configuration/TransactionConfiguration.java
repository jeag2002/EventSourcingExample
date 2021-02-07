package org.crealogix.gateway.configuration;

import java.util.UUID;

import javax.persistence.EntityManager;

import org.crealogix.infraestructure.interfaces.dispatcher.sync.SynchronizedCommandDispatcher;
import org.crealogix.infraestructure.interfaces.events.EventConsumer;
import org.crealogix.infraestructure.interfaces.events.EventPublisher;
import org.crealogix.infraestructure.interfaces.events.sync.InMemoryEventPublisher;
import org.crealogix.infraestructure.interfaces.repository.Repository;
import org.crealogix.infraestructure.interfaces.repository.inmemory.InMemoryRepository;
import org.crealogix.transaction.client.events.actions.ClientEventConsumer;
import org.crealogix.transaction.data.aggregates.Client;
import org.crealogix.transaction.data.aggregates.Transaction;
import org.crealogix.transaction.history.events.actions.HistoryEventConsumer;
import org.crealogix.transaction.transaction.TransactionCommandContext;
import org.crealogix.transaction.transaction.events.actions.TransactionEventConsumer;
import org.crealogix.transaction.transaction.repository.RepositoryTransactionDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class TransactionConfiguration {
	
	private final Logger logger = LoggerFactory.getLogger(TransactionConfiguration.class);
	
	 @Autowired
	 @Qualifier("HistoryEventConsumer")
	 private HistoryEventConsumer register;
	 
	 
	
	 @Bean(name="repositoryTransaction")
	 @Profile("local")
	 public Repository<Transaction, UUID> repositoryLocal() {
		 	logger.info("[TRANSACTION-CONFIG] LOAD CONFIG REPOSITORY LOCAL");
			return new InMemoryRepository<>();
	 }
	 
	 
	 @Bean(name="repositoryTransaction")
	 @Profile("db")
	 public Repository<Transaction, UUID> repositoryDB(@Autowired @Qualifier("entityManagerDB") EntityManager em) {
		 	logger.info("[TRANSACTION-CONFIG] LOAD CONFIG REPOSITORY DB");
			return new RepositoryTransactionDB(em);
	 }
	 
	 
	  
	  @Bean(name="eventPublisherTransaction")
	  public InMemoryEventPublisher<UUID> transPublisher(
			  final @Autowired @Qualifier("repositoryClient") Repository<Client, UUID> clientRepository,
			  final @Autowired @Qualifier("repositoryTransaction") Repository<Transaction, UUID> transRepository) {
		     logger.info("[TRANSACTION-CONFIG] CREATE EVENT PUBLISHER");
		     InMemoryEventPublisher<UUID> eventPublishers = new InMemoryEventPublisher<>();
		     eventPublishers.registerEventConsumer(new TransactionEventConsumer(clientRepository, transRepository));
		     eventPublishers.registerEventConsumer((EventConsumer)register);
		     
		     return eventPublishers;
	  }
	  
	  @Bean(name="transactionCommandContext")
	  public TransactionCommandContext createTransactionCommandContext(
			    final @Autowired @Qualifier("repositoryClient") Repository<Client, UUID> clientRepository,
	            final @Autowired @Qualifier("repositoryTransaction") Repository<Transaction, UUID> transRepository,
	            final @Autowired @Qualifier("eventPublisherTransaction") EventPublisher<UUID> transPublisher) {
		    
		    logger.info("[TRANSACTION-CONFIG] CREATE COMMAND CONTEXT");
	        return new TransactionCommandContext(clientRepository, transRepository, transPublisher);
	  }
	  
	  
	  @Bean(name="TransDispatcher")
	  public SynchronizedCommandDispatcher<TransactionCommandContext, Transaction, UUID> createClientCommandDispatcher(TransactionCommandContext context) {
		  logger.info("[TRANSACTION-CONFIG] CREATE COMMAND DISPATCHER");
		  return new SynchronizedCommandDispatcher<TransactionCommandContext, Transaction, UUID>(context);
	  }

	
}
