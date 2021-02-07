package org.crealogix.gateway.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.crealogix.gateway.controller.bean.CreateTransactionRequestBean;
import org.crealogix.infraestructure.interfaces.dispatcher.sync.SynchronizedCommandDispatcher;
import org.crealogix.infraestructure.interfaces.repository.Repository;
import org.crealogix.transaction.data.aggregates.Client;
import org.crealogix.transaction.data.aggregates.Transaction;
import org.crealogix.transaction.data.entities.Account;
import org.crealogix.transaction.enums.TransactionState;
import org.crealogix.transaction.payment.events.actions.PaymentEventConsumer;
import org.crealogix.transaction.payment.events.actions.PaymentGatewayEventConsumer;
import org.crealogix.transaction.transaction.TransactionCommandContext;
import org.crealogix.transaction.transaction.actions.ChangeTransactionCommand;
import org.crealogix.transaction.transaction.actions.CreateTransactionCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("trans")
public class GatewayTransactionController {

	private final Logger logger = LoggerFactory.getLogger(GatewayTransactionController.class);
	
	@Autowired
    @Qualifier("TransDispatcher")	
    private SynchronizedCommandDispatcher<TransactionCommandContext, Transaction, UUID> transDispatcher;
	
	
	@Autowired
	@Qualifier("repositoryClient")
	private Repository<Client, UUID> clientRepository;
	
	
	@Autowired
	@Qualifier("repositoryTransaction")
	private Repository<Transaction, UUID> transRepository;
	
	
	@Autowired
	@Qualifier("PaymentEventConsumer")
	private PaymentEventConsumer payEventConsumer;
	
	@Autowired
	@Qualifier("PaymentGatewayEventConsumer")
	private PaymentGatewayEventConsumer payGatewayEventConsumer;
	
	
	@RequestMapping(value = "/getTransaction/{transaction}", 
			method = RequestMethod.GET, 
		    produces = MediaType.APPLICATION_JSON_VALUE, 
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Transaction> getClient(@PathVariable("transaction") String id_transaction) {
		
		Transaction transaction;
		
		try {
			transaction = transRepository.load(UUID.fromString(id_transaction)).orElseThrow();
		} catch (Exception e) {
			logger.warn("[WARN] SOMETHING HAPPENED WHILE EXECUTING QUERY TRANSACTION COMMAND " +  e.getMessage());
			transaction = Transaction.builder().build();
		}
		return ResponseEntity.accepted().body(transaction);
	}
	
	
	@RequestMapping(value = "/changeStateTransaction/{id}/{flag}", 
					method = RequestMethod.PATCH,
					produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Transaction> changeFlagTransaction(@PathVariable("id") String id_transaction, @PathVariable("flag") String flag_transaction) {
		
		Transaction transaction;
		try {
			ChangeTransactionCommand cTC = new ChangeTransactionCommand(UUID.fromString(id_transaction), TransactionState.getByName(flag_transaction));
			transDispatcher.dispatch(cTC);
			transaction = transRepository.load(UUID.fromString(id_transaction)).orElseThrow();
		} catch (Exception e) {
			logger.warn("[WARN] SOMETHING HAPPENED WHILE EXECUTING QUERY TRANSACTION COMMAND " +  e.getMessage());
			transaction = Transaction.builder().build();
		}
		
		
		return ResponseEntity.accepted().body(transaction);
		
	}
	
	
	
	
	@RequestMapping(value = "/createTransaction", 
			method = RequestMethod.POST, 
		    produces = MediaType.APPLICATION_JSON_VALUE, 
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Transaction> createTransaction(@RequestBody CreateTransactionRequestBean transactionRequestBean){
		
		ArrayList<Client> clientList = new ArrayList<Client>();
		ArrayList<Account> accountList = new ArrayList<Account>();
		
		Transaction trans;
 		
		
		Client client_source = clientRepository.load(UUID.fromString(transactionRequestBean.getClientSource())).orElseThrow();
		
		clientList.add(client_source);
		
	    Optional<Account> account_source = 
	    client_source
	    .getAccounts()
	    .stream()
	    .filter(e->e.getId().equals(UUID.fromString(transactionRequestBean.getAccountSource())))
	    .findAny();
	    		
	    if (account_source.isEmpty()) {throw new RuntimeException("Account not valid");}
	    else {
	    	accountList.add(account_source.get());
	    }
	    		
		
		Client client_dest = clientRepository.load(UUID.fromString(transactionRequestBean.getClientDest())).orElseThrow();
		
		clientList.add(client_dest);
		
		 Optional<Account> account_dest = 
	     client_dest
		 .getAccounts()
		 .stream()
		 .filter(e->e.getId().equals(UUID.fromString(transactionRequestBean.getAccountDest())))
		 .findAny();
				    		
	     if (account_dest.isEmpty()) {throw new RuntimeException("Account not valid");}
	     else {
	    	 accountList.add(account_dest.get());
	     }
	     
	     
	     final var idTransactionStr = UUID.randomUUID();
	     
	     
	     
	     CreateTransactionCommand cTT = new CreateTransactionCommand(idTransactionStr, transactionRequestBean.getNameTransaction() ,clientList, accountList, transactionRequestBean.getImportTrans()); 
	     
	     try {
	    	 transDispatcher.dispatch(cTT);
	    	 trans = transRepository.load(idTransactionStr).orElseThrow();
	     }catch(Exception e) {
			logger.warn("[WARN] SOMETHING HAPPENED WHILE EXECUTING CREATE TRANSACTION COMMAND " +  e.getMessage());
			trans = Transaction.builder().build(); 
	     }
		
		
		return ResponseEntity.accepted().body(trans);
	}
	
	
	
	
	
	
}
