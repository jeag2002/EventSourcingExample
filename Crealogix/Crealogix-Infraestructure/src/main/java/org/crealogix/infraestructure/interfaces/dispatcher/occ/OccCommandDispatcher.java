package org.crealogix.infraestructure.interfaces.dispatcher.occ;


import org.crealogix.infraestructure.interfaces.dispatcher.CommandContext;
import org.crealogix.infraestructure.interfaces.dispatcher.CommandDispatcher;
import org.crealogix.infraestructure.interfaces.entities.actions.Command;
import org.crealogix.infraestructure.interfaces.entities.model.AggregateRoot;
import org.crealogix.infraestructure.interfaces.exceptions.CommandException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OccCommandDispatcher<C extends CommandContext<T, U>, T extends AggregateRoot<U>, U>
        implements CommandDispatcher<C, T, U> {

    private static final Logger LOG = LoggerFactory.getLogger(OccCommandDispatcher.class);

    private final C context;

    public OccCommandDispatcher(final C context) {
        this.context = context;
    }

    public C getContext() {
        return context;
    }

    @Override
    public void dispatch(final Command<C, T, U> command) {
        var retry = true;
        while (retry) {
            try {
                command.execute(context);
                retry = false;
            } catch (CommandException e) {
                LOG.warn("Reintentando comando por estado inconsistente", e);
            }
        }
    }
}
