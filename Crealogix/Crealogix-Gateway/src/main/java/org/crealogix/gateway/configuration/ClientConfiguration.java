package org.crealogix.gateway.configuration;

import java.util.UUID;

import javax.persistence.EntityManager;

import org.crealogix.infraestructure.interfaces.dispatcher.sync.SynchronizedCommandDispatcher;
import org.crealogix.infraestructure.interfaces.events.EventConsumer;
import org.crealogix.infraestructure.interfaces.events.EventPublisher;
import org.crealogix.infraestructure.interfaces.events.sync.InMemoryEventPublisher;
import org.crealogix.infraestructure.interfaces.repository.Repository;
import org.crealogix.infraestructure.interfaces.repository.inmemory.InMemoryRepository;
import org.crealogix.transaction.client.ClientCommandContext;
import org.crealogix.transaction.client.events.actions.ClientEventConsumer;
import org.crealogix.transaction.client.repository.RepositoryClientDB;
import org.crealogix.transaction.data.aggregates.Client;
import org.crealogix.transaction.history.events.actions.HistoryEventConsumer;
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
public class ClientConfiguration {
	
	 private final Logger logger = LoggerFactory.getLogger(ClientConfiguration.class);
	 
	 @Autowired
	 @Qualifier("HistoryEventConsumer")
	 private HistoryEventConsumer register; 
	
 
	 @Bean(name="repositoryClient")
	 @Profile("local")
	 public Repository<Client, UUID> repositoryLocal() {
		 	logger.info("[CLIENT-CONFIG] LOAD CONFIG REPOSITORY LOCAL");
			return new InMemoryRepository<>();
	 }

	 @Bean(name="repositoryClient")
	 @Profile("db")
	 public Repository<Client, UUID> repositoryDB(@Autowired @Qualifier("entityManagerDB") EntityManager em) {
		 	logger.info("[CLIENT-CONFIG] LOAD CONFIG REPOSITORY DB");
			return new RepositoryClientDB(em);
		   //return db;
	 }
	 
	  
	  @Bean(name="eventPublisherClient")
	  public InMemoryEventPublisher<UUID> clientPublisher(final @Autowired @Qualifier("repositoryClient") Repository<Client, UUID> clientRepository) {
		     logger.info("[CLIENT-CONFIG] CREATE EVENT PUBLISHER");
		     InMemoryEventPublisher<UUID> eventPublishers = new InMemoryEventPublisher<>();
		     eventPublishers.registerEventConsumer(new ClientEventConsumer(clientRepository));
		     eventPublishers.registerEventConsumer((EventConsumer)register);
		     return eventPublishers;
	  }

	  @Bean(name="clientCommandContext")
	  public ClientCommandContext createClientCommandContext(
	            final @Autowired @Qualifier("repositoryClient") Repository<Client, UUID> clientRepository,
	            final @Autowired @Qualifier("eventPublisherClient") EventPublisher<UUID> clientPublisher) {
		    
		    logger.info("[CLIENT-CONFIG] CREATE COMMAND CONTEXT");
	        return new ClientCommandContext(clientRepository, clientPublisher);
	  }
	  
	  
	  @Bean(name="ClientDispatcher")
	  public SynchronizedCommandDispatcher<ClientCommandContext, Client, UUID> createClientCommandDispatcher(ClientCommandContext context) {
		  logger.info("[CLIENT-CONFIG] CREATE COMMAND DISPATCHER");
		  return new SynchronizedCommandDispatcher<ClientCommandContext, Client, UUID>(context);
	  }
	  	
	

}
