package org.dontpanic.jpa.repository;

import org.dontpanic.jpa.entity.Movie;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MovieRepository extends CrudRepository<Movie, Long> {
    List<Movie> findByTitle(String title);
}
