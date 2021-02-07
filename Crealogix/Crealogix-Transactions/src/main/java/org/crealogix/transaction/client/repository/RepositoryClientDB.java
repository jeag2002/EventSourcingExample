package org.crealogix.transaction.client.repository;

import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.sql.DataSource;

import org.crealogix.infraestructure.interfaces.repository.Repository;
import org.crealogix.transaction.data.aggregates.Client;
import org.crealogix.transaction.data.aggregates.Transaction;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RepositoryClientDB implements Repository<Client, UUID> {
	
	private final Logger logger = LoggerFactory.getLogger(RepositoryClientDB.class);
	
	private EntityManager em;
	
	public RepositoryClientDB(EntityManager em) {
		this.em = em;
	}
	
	
	@Override
	public void save(Client instance) {
		
		logger.info("[REPOSITORY CLIENT DB] SAVE");
		em.getTransaction().begin();
		try {
			em.persist(instance);
			em.flush();
			em.getTransaction().commit();
		}catch(Exception e) {
			logger.warn("[REPOSITORY CLIENT DB] " + e.getMessage());
			em.getTransaction().rollback();
		}
		
	}
	
	
	@Override
	public void update(Client instance) {
		
		logger.info("[REPOSITORY CLIENT DB] SAVE");
		em.getTransaction().begin();
		try {
			em.merge(instance);
			em.flush();
			em.getTransaction().commit();
		}catch(Exception e) {
			logger.warn("[REPOSITORY CLIENT DB] " + e.getMessage());
			em.getTransaction().rollback();
		}
		
		
	}

	
	
	

	@Override
	public Optional<Client> load(UUID id) {
		
		logger.info("[REPOSITORY CLIENT DB] QUERY");
		
		Optional<Client> result = Optional.of(Client.builder().build());
		
		try {
			
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Client> query = cb.createQuery(Client.class);
			Root<Client> clients = query.from(Client.class);
			Predicate p1 = cb.equal(clients.get("id"),id);
			query.select(clients).where(p1);
			Client cl = em.createQuery(query).getSingleResult();
			
			result = Optional.of(cl);
			
			
		} catch (Exception e) {
			
			logger.warn("[REPOSITORY CLIENT DB] QUERY BY ID. SOMETHING HAPPENED " + e.getMessage());
			result = Optional.empty();
			
		}
		
	
		return result;
	}

	@Override
	public List<Client> find(java.util.function.Predicate<Client> filter) {
		return null;
	}


	@Override
	public void delete(UUID id) {
		
		logger.info("[REPOSITORY CLIENT DB] SAVE");
		
		em.getTransaction().begin();
		try {
			Optional<Client> client = this.load(id);
			em.remove(client.get());
			em.flush();
		}catch(Exception e) {
			logger.warn("[REPOSITORY CLIENT DB] " + e.getMessage(),e);
		}
		em.getTransaction().commit();
		
	}


	
}
