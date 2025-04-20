package noteapp;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Note implements Serializable {
    private String title;
    private String content;
    private String category;

    // Constructor
    public Note(String title, String content, String category) {
        this.title = title;
        this.content = content;
        this.category = category;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "Note{" +
                "title='" + title + '\'' +
                ", category='" + category + '\'' +
                '}';
    }
}
