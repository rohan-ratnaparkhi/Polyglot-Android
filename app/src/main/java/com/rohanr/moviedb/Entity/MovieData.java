package com.rohanr.moviedb.Entity;

/**
 * Created by rohanr on 6/15/16.
 */

public class MovieData {
    String imageUrl;
    String title;
    Integer movieId;

   public MovieData(String imageUrl, String title, Integer movieId){
        this.imageUrl = imageUrl;
        this.title = title;
        this.movieId = movieId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getMovieId() {
        return movieId;
    }

    public void setMovieId(Integer movieId) {
        this.movieId = movieId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
