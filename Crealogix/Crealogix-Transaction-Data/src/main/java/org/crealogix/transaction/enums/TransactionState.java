package org.crealogix.transaction.enums;

public enum TransactionState {
	
	NO_ACTION,
	CREATED,
	CONFIRMED,
	CANCELLED,
	FINALIZED,
	STOPPED_TO,
	STOPPED_GT,
	STOPPED_USR;
	
	public static TransactionState getByName(String name) {
		
		TransactionState TS[] = TransactionState.values();
		TransactionState value = TransactionState.NO_ACTION;
		
		for(int i=0; i<TS.length; i++) {
			TransactionState ts = TS[i];
			if (ts.name().equalsIgnoreCase(name)) {
				value = ts;
				break;
			}
		}
		
		return value;
	}
	
	
}
