package org.crealogix.gateway.configuration;

import javax.persistence.EntityManager;

import org.crealogix.infraestructure.interfaces.repository.inmemory.DummyPlatformTransactionManager;
import org.crealogix.infraestructure.interfaces.task.TaskScheduler;
import org.crealogix.transaction.data.aggregates.Client;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
@Profile("db")
public class DatabaseConfiguration {

	@Bean(name="entityManagerDB")
	EntityManager generateEntityManager() {
		
		SessionFactory sessionFactory = new org.hibernate.cfg.Configuration().configure().buildSessionFactory();
		Session session = sessionFactory.openSession();
		EntityManager em = session.getEntityManagerFactory().createEntityManager();
		return em;
		
	}
	
	
	
	@Bean
	public PlatformTransactionManager transactionManager() {
	       return new DummyPlatformTransactionManager();
	}
	  
}
