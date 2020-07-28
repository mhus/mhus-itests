package de.mhus.examples.rest;

import de.mhus.lib.annotations.generic.Public;

public class Book {

    @Public(writable = false)
    private String isbn;
    @Public
    private String title;
    @Public
    private String author;
    @Public
    private String description;
    @Public
    private int createdYear;
    
    public String getIsbn() {
        return isbn;
    }
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public int getCreatedYear() {
        return createdYear;
    }
    public void setCreatedYear(int createdYear) {
        this.createdYear = createdYear;
    }
}
