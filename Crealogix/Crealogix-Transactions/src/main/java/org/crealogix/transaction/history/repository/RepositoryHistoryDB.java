package org.crealogix.transaction.history.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

import javax.persistence.EntityManager;

import org.crealogix.infraestructure.interfaces.repository.Repository;
import org.crealogix.transaction.data.entities.History;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RepositoryHistoryDB implements Repository<History, UUID>{
	
	private final Logger logger = LoggerFactory.getLogger(RepositoryHistoryDB.class);
	
	private EntityManager em;
	
	public RepositoryHistoryDB(EntityManager em) {
		this.em = em;
	}

	@Override
	public void save(History instance) {
		
		logger.info("[REPOSITORY DB] SAVE");
		em.getTransaction().begin();
		try {
			em.persist(instance);
			em.flush();
			em.getTransaction().commit();
		}catch(Exception e) {
			em.getTransaction().rollback();
			logger.warn("[REPOSITORY CLIENT DB] " + e.getMessage(),e);
		}
		
	}
	
	@Override
	public void update(History instance) {
		
		logger.info("[REPOSITORY DB] SAVE");
		em.getTransaction().begin();
		try {
			em.merge(instance);
			em.flush();
			em.getTransaction().commit();
		}catch(Exception e) {
			em.getTransaction().rollback();
			logger.warn("[REPOSITORY CLIENT DB] " + e.getMessage(),e);
		}
		
		
		
	}
	

	@Override
	public Optional<History> load(UUID id) {
		return null;
	}

	@Override
	public List<History> find(Predicate<History> filter) {
		return null;
	}

	@Override
	public void delete(UUID id) {
	}

	
	
}
