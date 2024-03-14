package org.dontpanic.jpa.repository;

import org.dontpanic.jpa.entity.Movie;
import org.dontpanic.jpa.entity.Star;
import org.hibernate.LazyInitializationException;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class MovieRepositoryTest {

    @Autowired private MovieRepository repository;
    @Autowired private TestEntityManager entityManager;
    private Statistics statistics;

    @BeforeEach
    void setUp() {
        SessionFactory sf = entityManager.getEntityManager().getEntityManagerFactory().unwrap(SessionFactory.class);
        statistics = sf.getStatistics();

        initDatabase();
        // Clear the EntityManager to force loading entities from the database
        entityManager.flush();
        entityManager.clear();

        // Enable statistics so we can verify execution counts
        statistics.setStatisticsEnabled(true);
        statistics.clear();
    }

    @Test
    void testCount() {
        assertEquals(8, repository.count());
    }

    @Test
    void testFindByTitle() {
        List<Movie> results = repository.findByTitle("Ghostbusters");
        entityManager.clear(); // Clear EM to prevent lazy loading. We want to prove that stars are lazy loaded
        assertThat(results, hasSize(1));
        Movie result = results.get(0);
        assertThat(result.getTitle(), equalTo("Ghostbusters"));
        assertThrows(LazyInitializationException.class, () -> result.getStars().size());
    }

    @Test
    void testFindByTitleEagerFetchStars() {
        List<Movie> results = repository.findByTitleEagerFetchStars("Ghostbusters");
        entityManager.clear(); // Clear EM to prevent lazy loading. We want to prove that stars have been eager loaded
        assertThat(results, hasSize(1));
        Movie result = results.get(0);
        assertThat(result.getTitle(), equalTo("Ghostbusters"));
        assertThat(result.getStars(), hasSize(4));
        assertThat(result.getStars(), containsInAnyOrder(
                hasProperty("lastName", equalTo("Aykroyd")),
                hasProperty("lastName", equalTo("Hudson")),
                hasProperty("lastName", equalTo("Murray")),
                hasProperty("lastName", equalTo("Ramis"))
        ));

        // Movie and Star entities were loaded with a single query
        assertEquals(1, statistics.getQueryExecutionCount());
    }

    /**
     * This test demonstrates what happens if we use a native SQL query with a join.
     * We get one Entity result per returned row and none have the joined data attached.
     * In this case we get 4 Ghostbusters movies, one for each movie / star result.
     */
    @Test
    void testFindByTitleNativeWithoutMapping() {
        List<Movie> results = repository.findByTitleNative("Ghostbusters");
        entityManager.clear(); // Clear EM to prevent lazy loading. We want to prove that stars are lazy loaded
        // Expect 4 Movie objects corresponding to each
        assertThat(results, hasSize(4));
        // Expect all results to be Ghostbusters
        for (Movie movie : results) {
            assertEquals("Ghostbusters", movie.getTitle());
            // Expect that Star entities are not loaded, even though they were included in the join
            assertThrows(LazyInitializationException.class, () -> movie.getStars().size());
        }
    }

    @Test
    void testFindByTitleNamedNativeQueryWithResultSetMapping() {
        List<MovieStarResult> results = repository.findByTitleNamedNativeQueryWithResultSetMapping("Ghostbusters");
        entityManager.clear(); // Clear EM to prevent lazy loading. We want to prove that stars have been eager loaded
        assertThat(results, hasSize(4));

        // MovieStarResults represent each unique Movie / Star tuple.
        // Merge them to a list of unique Movies and Stars and set entity relations.
        Collection<Movie> movies = MovieStarResult.getMovies(results);

        assertThat(movies, hasSize(1));
        Movie movie = movies.iterator().next();
        assertNotNull(movie);
        assertThat(movie.getTitle(), equalTo("Ghostbusters"));
        assertThat(movie.getStars(), hasSize(4));
        assertThat(movie.getStars(), containsInAnyOrder(
                hasProperty("lastName", equalTo("Aykroyd")),
                hasProperty("lastName", equalTo("Hudson")),
                hasProperty("lastName", equalTo("Murray")),
                hasProperty("lastName", equalTo("Ramis"))
        ));

        // Movie and Star entities were loaded with a single query
        assertEquals(1, statistics.getQueryExecutionCount());
    }

    /**
     * Initialize database with some test data.
     */
    private void initDatabase() {
        Star aykroyd = new Star("Dan", "Aykroyd");
        Star bellushi = new Star("John", "Bellushi");
        Star fisher = new Star("Carrie", "Fisher");
        Star ford = new Star("Harrison", "Ford");
        Star hamill = new Star("Mark", "Hamill");
        Star hudson = new Star("Ernie", "Hudson");
        Star murray = new Star("Bill", "Murray");
        Star ramis = new Star("Harold", "Ramis");

        Movie starWarsIV = new Movie("Star Wars IV: A New Hope");
        Movie starWarsV = new Movie("Star Wars V: The Empire Strikes Back");
        Movie starWarsVI = new Movie("Star Wars VI: Return of the Jedi");
        Movie indiana1 = new Movie("Raiders of the Lost Ark");
        Movie indiana2 = new Movie("Temple of Doom");
        Movie indiana3 = new Movie("Last Crusade");
        Movie bluesBrothers = new Movie("Blues Brothers");
        Movie ghostbusters = new Movie("Ghostbusters");

        starWarsIV.addStars(fisher, ford, hamill);
        starWarsV.addStars(fisher, ford, hamill);
        starWarsVI.addStars(fisher, ford, hamill);
        indiana1.addStars(ford);
        indiana2.addStars(ford);
        indiana3.addStars(ford);
        bluesBrothers.addStars(aykroyd, bellushi, fisher);
        ghostbusters.addStars(aykroyd, hudson, murray, ramis);

        repository.saveAll(List.of(starWarsIV, starWarsV, starWarsVI, indiana1, indiana2, indiana3, bluesBrothers, ghostbusters));
    }
}