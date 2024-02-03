package org.dontpanic.jpa.repository;

import org.dontpanic.jpa.entity.Movie;
import org.springframework.data.repository.CrudRepository;

public interface MovieRepository extends CrudRepository<Movie, Long> {
}
