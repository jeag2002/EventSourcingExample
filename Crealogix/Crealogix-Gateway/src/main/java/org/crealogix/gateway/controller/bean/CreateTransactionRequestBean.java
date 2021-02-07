package org.crealogix.gateway.controller.bean;

import java.io.Serializable;

import lombok.Data;

@Data
public class CreateTransactionRequestBean implements Serializable {
    
	private static final long serialVersionUID = 1L;
	
	private String nameTransaction;
	
	private String clientSource;
	
	private String accountSource;
	
	private String clientDest;
	
	private String accountDest;
	
	private float importTrans;
	
	
	
}
