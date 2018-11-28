package com.stellar.xueyingbai.travelandentertainmentsearch;

public class PlacesReviews {
    private String author_name;
    private String author_url;
    private String profile_photo_url;
    private int rating;
    private String text;
    private String time;

    public PlacesReviews() {

    }

    public PlacesReviews(String author_name, String author_url, String profile_photo_url, int rating, String text, String time) {
        this.author_name = author_name;
        this.author_url = author_url;
        this.profile_photo_url = profile_photo_url;
        this.rating = rating;
        this.text = text;
        this.time = time;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }

    public String getAuthor_name() {
        return author_name;
    }

    public void setAuthor_url(String author_url) {
        this.author_url = author_url;
    }

    public String getAuthor_url() {
        return author_url;
    }

    public void setProfile_photo_url(String profile_photo_url) {
        this.profile_photo_url = profile_photo_url;
    }

    public String getProfile_photo_url() {
        return profile_photo_url;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getRating() {
        return rating;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }
}
