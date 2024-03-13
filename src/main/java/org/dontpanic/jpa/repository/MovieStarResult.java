package org.dontpanic.jpa.repository;

public class MovieStarResult {

    private final String movieTitle;
    private final String starFirstName;
    private final String starLastName;

    public MovieStarResult(String movieTitle, String starFirstName, String starLastName) {
        this.movieTitle = movieTitle;
        this.starFirstName = starFirstName;
        this.starLastName = starLastName;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public String getStarFirstName() {
        return starFirstName;
    }

    public String getStarLastName() {
        return starLastName;
    }
}
