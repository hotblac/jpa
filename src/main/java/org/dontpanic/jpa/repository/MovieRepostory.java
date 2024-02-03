package org.dontpanic.jpa.repository;

import org.dontpanic.jpa.entity.Movie;
import org.springframework.data.repository.CrudRepository;

public interface MovieRepostory extends CrudRepository<Movie, Long> {
}
