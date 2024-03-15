package org.dontpanic.jpa.entity;

import jakarta.persistence.*;
import org.dontpanic.jpa.repository.MovieStarResult;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Entity
@NamedNativeQuery(name = "Movie.findByTitleNamedNativeQueryWithResultSetMapping", query = """
    SELECT m.id m_id, m.title m_title, s.id s_id, s.first_name s_firstName, s.last_name s_lastName
    FROM Movie m
    LEFT JOIN movie_star ms ON m.id = ms.movie_id
    LEFT JOIN Star s ON ms.star_id = s.id
    WHERE m.title = :title
    """, resultSetMapping = "movieStarResult", resultClass = MovieStarResult.class)
@SqlResultSetMapping(name="movieStarResult", classes = @ConstructorResult(
        targetClass = MovieStarResult.class,
        columns = {
                @ColumnResult(name="m_id"),
                @ColumnResult(name="m_title"),
                @ColumnResult(name="s_id"),
                @ColumnResult(name="s_firstName"),
                @ColumnResult(name="s_lastName")
        }
))
public class Movie {


    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    private String title;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "movie_star", joinColumns = @JoinColumn(name = "movie_id"), inverseJoinColumns = @JoinColumn(name = "star_id"))
    private Set<Star> stars = new HashSet<>();

    public Movie() {
    }

    public Movie(String title) {
        this.title = title;
    }

    public Movie(Long id, String title) {
        this.id = id;
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Set<Star> getStars() {
        return stars;
    }

    public void setStars(Set<Star> stars) {
        this.stars = stars;
    }

    public void addStars(Star... star) {
        stars.addAll(Arrays.asList(star));
    }
}
