package org.crealogix.transaction.enums;

public enum ActionState {
	
	NO_ACTION,
	CREATE_CLIENT,
	DELETE_CLIENT,
	CREATE_TRANSACTION,			//transaction_created
	CONFIRM_TRANSACTION,		//transaction_confirmed --> initialize payment
	CANCEL_TRANSACTION,			//transaction_cancelled --> avoid payment
	STOP_GATEWAY_TRANSACTION,	//transaction_stopped --> gateway_failure
	STOP_CANCEL_TRANSACTION,	//transaction_stopped --> cancelled by user
	STOP_TIMEOUT_TRANSACTION,
	FINALIZE_TRANSACTION;		//transaction_finalized

}
