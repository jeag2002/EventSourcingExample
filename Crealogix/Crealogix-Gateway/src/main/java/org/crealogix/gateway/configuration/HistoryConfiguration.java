package org.crealogix.gateway.configuration;

import java.util.UUID;

import javax.persistence.EntityManager;

import org.crealogix.infraestructure.interfaces.repository.Repository;
import org.crealogix.infraestructure.interfaces.repository.inmemory.InMemoryRepository;
import org.crealogix.transaction.client.repository.RepositoryClientDB;
import org.crealogix.transaction.data.aggregates.Client;
import org.crealogix.transaction.data.entities.History;
import org.crealogix.transaction.history.events.actions.HistoryEventConsumer;
import org.crealogix.transaction.history.repository.RepositoryHistoryDB;
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
public class HistoryConfiguration {
	
	private final Logger logger = LoggerFactory.getLogger(HistoryConfiguration.class);
	
	
	 @Bean(name="repositoryHistory")
	 @Profile("local")
	 public Repository<History, UUID> repositoryLocal() {
		 	logger.info("[HISTORY-CONFIG] LOAD CONFIG REPOSITORY LOCAL");
			return new InMemoryRepository<>();
	 }

	 @Bean(name="repositoryHistory")
	 @Profile("db")
	 public Repository<History, UUID> repositoryDB(@Autowired @Qualifier("entityManagerDB") EntityManager em) {
		 	logger.info("[HISTORY-CONFIG] LOAD CONFIG REPOSITORY DB");
			return new RepositoryHistoryDB(em);
	 }
	 
	 @Bean(name="HistoryEventConsumer")
	 public HistoryEventConsumer createEventConsumer(@Autowired @Qualifier("repositoryHistory") Repository<History,UUID> historyRepository) {
		 logger.info("[HISTORY-CONFIG] CREATE EVENT CONSUMER");
		 return new HistoryEventConsumer(historyRepository);
	 }
	

}
