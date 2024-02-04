package org.dontpanic.jpa.repository;

import org.dontpanic.jpa.entity.Movie;
import org.dontpanic.jpa.entity.Star;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class MovieRepositoryTest {

    @Autowired private MovieRepository repository;
    @Autowired private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        initDatabase();
        // Clear the EntityManager to force loading entities from the database
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void testCount() {
        assertEquals(8, repository.count());
    }

    @Test
    void testFindByTitle() {
        List<Movie> results = repository.findByTitle("Ghostbusters");
        entityManager.clear();
        assertThat(results, hasSize(1));
        Movie result = results.get(0);
        assertThat(result.getTitle(), equalTo("Ghostbusters"));
        assertThrows(LazyInitializationException.class, () -> result.getStars().size());
    }

    @Test
    void testFindByTitleEagerFetchStars() {
        List<Movie> results = repository.findByTitleEagerFetchStars("Ghostbusters");
        entityManager.clear();
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