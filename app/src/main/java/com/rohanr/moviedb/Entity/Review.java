package com.rohanr.moviedb.Entity;

/**
 * Created by rohanr on 6/15/16.
 */
public class Review {

    private String reviewer;
    private String comment;


    public String getReviewer() {
        return reviewer;
    }

    public void setReviewer(String reviewer) {
        this.reviewer = reviewer;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
