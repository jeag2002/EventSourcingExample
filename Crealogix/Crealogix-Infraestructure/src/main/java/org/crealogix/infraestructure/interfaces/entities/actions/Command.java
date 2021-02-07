package org.crealogix.infraestructure.interfaces.entities.actions;

import org.crealogix.infraestructure.interfaces.dispatcher.CommandContext;
import org.crealogix.infraestructure.interfaces.entities.model.AggregateRoot;

public interface Command<C extends CommandContext<T, U>, T extends AggregateRoot<U>, U> {
    
    U getAggregateRootId();

    void execute(final C context);
}
