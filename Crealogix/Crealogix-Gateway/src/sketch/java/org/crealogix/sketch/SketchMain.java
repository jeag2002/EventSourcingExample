package org.crealogix.sketch;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.crealogix.gateway.controller.bean.CreateTransactionRequestBean;
import org.crealogix.transaction.data.aggregates.Client;
import org.crealogix.transaction.data.aggregates.Transaction;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SketchMain {

	public static void main(String[] args) throws ClientProtocolException, IOException, URISyntaxException {
		


		
		
		
		RestTemplate rest = new RestTemplate();
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setConnectTimeout(10000);
		requestFactory.setReadTimeout(10000);

		rest.setRequestFactory(requestFactory);
		
		
		
		HttpHeaders headers = new HttpHeaders();
		
		ObjectMapper mapper = new ObjectMapper();
		
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		
		////////////////////////////////////////////////////////////// CLIENT_1
		String client_1 = "{\r\n" + 
				"    \"version\": 1,\r\n" + 
				"    \"idStr\": \"client_1\",\r\n" + 
				"    \"enabled\": true,\r\n" + 
				"    \"enabledMultiAccountPay\": true,\r\n" + 
				"    \"accounts\": [\r\n" + 
				"        {\r\n" + 
				"            \"id\":\"5de124b5-9741-4fef-8ace-64e863e5ea77\",\r\n" + 
				"            \"idStr\":\"account_1\",\r\n" + 
				"            \"ccc\":\"ES50-2801-2222-22-222222222\",\r\n" + 
				"            \"importValue\": 1000.00\r\n" + 
				"        }\r\n" + 
				"    ]\r\n" + 
				"}";
		
		
		Client client_1_req = mapper.readValue(client_1, Client.class);
		
		HttpEntity<Client> entity = new HttpEntity<Client>(client_1_req,headers);
		String URL = "http://localhost:8080/client/createClient";
		
		ResponseEntity<Client> response = rest.exchange(URL, HttpMethod.POST, entity, Client.class);
		Client client_1_res = response.getBody();
		
		//////////////////////////////////////////////////////////////////////////////////////////////
		
		
		////////////////////////////////////////////////////////////////// CLIENT_2
		String client_2 = "{\r\n" + 
				"    \"version\": 2,\r\n" + 
				"    \"idStr\": \"client_test\",\r\n" + 
				"    \"enabled\": true,\r\n" + 
				"    \"enabledMultiAccountPay\": true,\r\n" + 
				"    \"accounts\": [\r\n" + 
				"        {\r\n" + 
				"            \"id\":\"88f2a7aa-5cbb-11eb-ae93-0242ac130005\",\r\n" + 
				"            \"idStr\":\"account_client_1\",\r\n" + 
				"            \"ccc\":\"1111111\",\r\n" + 
				"            \"importValue\": 1200.00\r\n" + 
				"        },\r\n" + 
				"        {\r\n" + 
				"            \"id\":\"9b140bb8-5cbb-11eb-ae93-0242ac130006\",\r\n" + 
				"            \"idStr\":\"account_client_2\",\r\n" + 
				"            \"ccc\":\"222222\",\r\n" + 
				"            \"importValue\": 1200.00\r\n" + 
				"\r\n" + 
				"        }\r\n" + 
				"    ]\r\n" + 
				"}";
		
		Client client_2_req = mapper.readValue(client_2, Client.class);
		
		entity = new HttpEntity<Client>(client_2_req,headers);
		response = rest.exchange(URL, HttpMethod.POST, entity, Client.class);
		Client client_2_res = response.getBody();
		
		//////////////////////////////////////////////////////////////////////////////////////////////
		
		
		String URL_1 = "http://localhost:8080/trans/createTransaction";
		
		String trans = "{\r\n" + 
				"    \"nameTransaction\": \"transaction_X\",\r\n" + 
				"    \"clientSource\":\""+client_1_res.getId().toString()+"\",\r\n" + 
				"    \"accountSource\":\""+client_1_res.getAccounts().get(0).getId().toString()+"\",\r\n" + 
				"    \"clientDest\":\""+client_2_res.getId().toString()+"\",\r\n" + 
				"    \"accountDest\":\""+client_2_res.getAccounts().get(0).getId().toString()+"\",\r\n" + 
				"    \"importTrans\":100.0\r\n" + 
				"}";
		
		
		CreateTransactionRequestBean trans_1 = mapper.readValue(trans, CreateTransactionRequestBean.class);
		HttpEntity<CreateTransactionRequestBean> entity_1 = new HttpEntity<CreateTransactionRequestBean>(trans_1,headers);
		ResponseEntity<Transaction> response_1 = rest.exchange(URL_1, HttpMethod.POST, entity_1, Transaction.class);
		
		Transaction response_2 = response_1.getBody();
		
		
		String URL_2 = "http://localhost:8080/trans/changeStateTransaction/"+response_2.getId().toString()+"/CONFIRMED";
		
	
		
		HttpEntity<HttpHeaders> entity_2 = new HttpEntity<>(headers);
		
		response_1 = rest.exchange(URL_2, HttpMethod.PATCH, entity_2,Transaction.class);
		response_2 = response_1.getBody();
		
		String data = mapper.writeValueAsString(response_2);
		System.out.println("DATA " +  data);
		
		
	}

}
