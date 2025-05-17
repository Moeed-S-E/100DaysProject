package app.entity;

public class Book {
    private int bookID;
    private String title;
    private String author;
    private String genre;
    private int quantity;

    public Book(int bookID, String title, String author, String genre, int quantity) {
        this.bookID = bookID;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.quantity = quantity;
    }

    public int getBookID() {
        return bookID;
    }
    public String getTitle() {
        return title;
    }
    public String getAuthor() {
        return author;
    }
    public String getGenre() {
        return genre;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public void setBookID(int bookID) {
        this.bookID = bookID;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public void setGenre(String genre) {
        this.genre = genre;
    }
    public void setTitle(String title) {
        this.title = title;
    }
}