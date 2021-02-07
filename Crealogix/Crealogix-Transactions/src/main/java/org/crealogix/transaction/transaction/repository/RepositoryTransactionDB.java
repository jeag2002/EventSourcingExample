package org.crealogix.transaction.transaction.repository;

import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.crealogix.infraestructure.interfaces.repository.Repository;
import org.crealogix.transaction.data.aggregates.Client;
import org.crealogix.transaction.data.aggregates.Transaction;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RepositoryTransactionDB implements Repository<Transaction, UUID> {
	
	private final Logger logger = LoggerFactory.getLogger(RepositoryTransactionDB.class);
	
	private EntityManager em;
	
	public RepositoryTransactionDB(EntityManager em) {		
		this.em = em;
	}
	
	
	@Override
	public void save(Transaction instance) {
		logger.info("[REPOSITORY TRANSACTION DB] SAVE");
		em.getTransaction().begin();
		try {
			em.persist(instance);
			em.flush();
			em.getTransaction().commit();
		}catch(Exception e) {
			logger.warn("[REPOSITORY TRANSACTION DB] " + e.getMessage(),e);
			em.getTransaction().rollback();
		}
		
	}
	
	@Override
	public void update(Transaction instance) {
		
		Transaction data = this.load(instance.getId()).orElseThrow();
		
		data.setState(instance.getState());
		data.setIdStr(instance.getIdStr());
		data.setVersion(instance.getVersion());
		
		logger.info("[REPOSITORY TRANSACTION DB] SAVE");
		em.getTransaction().begin();
		try {
			//em.merge(instance);
			em.merge(data);
			em.flush();
			em.getTransaction().commit();
		}catch(Exception e) {
			logger.warn("[REPOSITORY TRANSACTION DB] " + e.getMessage(),e);
			em.getTransaction().rollback();
		}
		
	}
	
	

	@Override
	public Optional<Transaction> load(UUID id) {
		logger.info("[REPOSITORY TRANSACTION DB] QUERY");
		Optional<Transaction> result = Optional.of(Transaction.builder().build());
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Transaction> query = cb.createQuery(Transaction.class);
			Root<Transaction> transactions = query.from(Transaction.class);
			Predicate p1 = cb.equal(transactions.get("id"),id);
			query.select(transactions).where(p1);
			Transaction cl = em.createQuery(query).getSingleResult();
			result = Optional.of(cl);
		} catch (Exception e) {
			logger.warn("[REPOSITORY TRANSACTION DB] QUERY BY ID. SOMETHING HAPPENED " + e.getMessage());
			result = Optional.empty();
		}
		return result;
	}

	@Override
	public List<Transaction> find(java.util.function.Predicate<Transaction> filter) {
		return null;
	}

	@Override
	public void delete(UUID id) {
		
		logger.info("[REPOSITORY TRANSACTION DB] SAVE");
		
		em.getTransaction().begin();
		try {
			Optional<Transaction> trans = this.load(id);
			em.remove(trans.get());
			em.flush();
			em.getTransaction().commit();
		}catch(Exception e) {
			logger.warn("[REPOSITORY TRANSACTION DB] " + e.getMessage(),e);
			em.getTransaction().rollback();
		}
		
		
		
		
	}

}
