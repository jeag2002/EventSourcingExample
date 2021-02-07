package org.crealogix.gateway.controller;

import java.util.UUID;

import javax.websocket.server.PathParam;

import org.crealogix.infraestructure.interfaces.dispatcher.CommandDispatcher;
import org.crealogix.infraestructure.interfaces.dispatcher.sync.SynchronizedCommandDispatcher;
import org.crealogix.infraestructure.interfaces.repository.Repository;
import org.crealogix.transaction.client.ClientCommandContext;
import org.crealogix.transaction.client.actions.RegisterClientCommand;
import org.crealogix.transaction.data.aggregates.Client;
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
@RequestMapping("client")
public class GatewayClientController {
	
	private final Logger logger = LoggerFactory.getLogger(GatewayClientController.class);

	@Autowired
    @Qualifier("ClientDispatcher")	
    private SynchronizedCommandDispatcher<ClientCommandContext, Client, UUID> clientDispatcher;
	
	
	@Autowired
	@Qualifier("repositoryClient")
	private Repository<Client, UUID> clientRepository;
	
	@RequestMapping(value = "/createClient", 
			method = RequestMethod.POST, 
		    produces = MediaType.APPLICATION_JSON_VALUE, 
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Client> createClient(@RequestBody Client client){
		
		final UUID idClientStr = UUID.randomUUID();
	    Client responseClient;
	    
	    RegisterClientCommand rCC = new RegisterClientCommand(idClientStr, client.getIdStr(),  client.getAccounts());
	    try {
			clientDispatcher.dispatch(rCC);
			responseClient = clientRepository.load(idClientStr).orElseThrow();
			
		} catch (Exception e) {
			responseClient = Client.builder().build();
			throw new RuntimeException("[WARN] SOMETHING HAPPENED WHILE EXECUTING CREATE CLIENT COMMAND " +  e.getMessage());	
		}
	    
		return ResponseEntity.accepted().body(responseClient);
	}
	
	
	@RequestMapping(value = "/getClient/{client}", 
			method = RequestMethod.GET, 
		    produces = MediaType.APPLICATION_JSON_VALUE, 
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Client> getClient(@PathVariable("client") String id_client) {
		
		Client responseClient;
		
		try {
			responseClient = clientRepository.load(UUID.fromString(id_client)).orElseThrow();
		} catch (Exception e) {
			logger.warn("[WARN] SOMETHING HAPPENED WHILE EXECUTING QUERY CLIENT COMMAND " +  e.getMessage());
			responseClient = Client.builder().build();
		}
		return ResponseEntity.accepted().body(responseClient);
		
		
	}
	
	
}
