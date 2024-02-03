package org.dontpanic.jpa.repository;

import org.dontpanic.jpa.entity.Movie;
import org.dontpanic.jpa.entity.Star;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.Assert.assertEquals;

@DataJpaTest
class MovieRepositoryTest {

    @Autowired private MovieRepository repository;

    @BeforeEach
    void setUp() {
        initDatabase();
    }

    @Test
    void count() {
        assertEquals(8, repository.count());
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

        repository.save(starWarsIV);
        repository.saveAll(List.of(starWarsIV, starWarsV, starWarsVI, indiana1, indiana2, indiana3, bluesBrothers, ghostbusters));
    }
}