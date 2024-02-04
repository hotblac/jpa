package org.dontpanic.jpa.repository;

import org.dontpanic.jpa.entity.Movie;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MovieRepository extends CrudRepository<Movie, Long> {
    List<Movie> findByTitle(String title);
    @Query("SELECT m FROM Movie m JOIN FETCH m.stars s WHERE m.title = :title")
    List<Movie> findByTitleEagerFetchStars(String title);
}
