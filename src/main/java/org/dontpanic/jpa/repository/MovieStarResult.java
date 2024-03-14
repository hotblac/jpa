package org.dontpanic.jpa.repository;

import org.dontpanic.jpa.entity.Movie;
import org.dontpanic.jpa.entity.Star;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Record representing a single Movie / Star tuple result.
 */
public record MovieStarResult (Long movieId, String movieTitle, Long starId, String starFirstName, String starLastName) {

    /**
     * Merge results into a Collection of distinct Movies.
     */
    public static Collection<Movie> getMovies(List<MovieStarResult> results) {
        Map<Long, Movie> movies = new HashMap<>();
        Map<Long, Star> stars = new HashMap<>();
        for (MovieStarResult result : results) {
            Movie movie = movies.computeIfAbsent(result.movieId, id -> result.getMovie());
            Star star = stars.computeIfAbsent(result.starId, id -> result.getStar());
            movie.addStars(star);
            star.addMovie(movie);
        }
        return movies.values();
    }

    private Movie getMovie() {
        return new Movie(movieId, movieTitle);
    }

    private Star getStar() {
        return new Star(starId, starFirstName, starLastName);
    }
}
