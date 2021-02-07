package org.crealogix.infraestructure.interfaces.repository;


import org.crealogix.infraestructure.interfaces.entities.model.Entity;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public interface Repository<T extends Entity<U>, U> {

    
    void save(final T instance);
    
    void update(final T instance);

   
    @NonNull
    Optional<T> load(final U id);
    
    
    @NonNull
    List<T> find(final Predicate<T> filter);

    void delete(final U id);
}
