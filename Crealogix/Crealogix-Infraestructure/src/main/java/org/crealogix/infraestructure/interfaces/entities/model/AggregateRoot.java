package org.crealogix.infraestructure.interfaces.entities.model;

public interface AggregateRoot<T> extends Entity<T> {
	
	long getVersion();
	
}
