package org.crealogix.infraestructure.interfaces.dispatcher.sync;

import org.crealogix.infraestructure.interfaces.dispatcher.CommandContext;
import org.crealogix.infraestructure.interfaces.dispatcher.CommandDispatcher;
import org.crealogix.infraestructure.interfaces.entities.actions.Command;
import org.crealogix.infraestructure.interfaces.entities.model.AggregateRoot;
import org.crealogix.infraestructure.interfaces.exceptions.CommandException;

public class SynchronizedCommandDispatcher<C extends CommandContext<T, U>, T extends AggregateRoot<U>, U>
        implements CommandDispatcher<C, T, U> {

    private final C context;

    public SynchronizedCommandDispatcher(final C context) {
        this.context = context;
    }

    public C getContext() {
        return context;
    }

    @Override
    public synchronized void dispatch(final Command<C, T, U> command)  {
            command.execute(context);
    }
}
