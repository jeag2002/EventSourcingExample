package org.crealogix.infraestructure.interfaces.dispatcher;

import org.crealogix.infraestructure.interfaces.entities.actions.Command;
import org.crealogix.infraestructure.interfaces.entities.model.AggregateRoot;

public interface CommandDispatcher<C extends CommandContext<T, U>, T extends AggregateRoot<U>, U> {
	
    void dispatch(final Command<C, T, U> command) throws Exception;
    
}
