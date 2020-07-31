package de.mhus.examples.jms;

import de.mhus.lib.core.MSystem;

public class Book {

    private String name;
    private String author;
    private String isbn;
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public String getIsbn() {
        return isbn;
    }
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
    
    @Override
    public String toString() {
        return MSystem.toString(this, name);
    }
}
