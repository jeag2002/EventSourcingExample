package org.crealogix.infraestructure.interfaces.entities.model;

import org.springframework.lang.NonNull;

public interface Entity<T> {
    
	@NonNull
    T getId();
    
}
