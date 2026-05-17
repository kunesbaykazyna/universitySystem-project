package utils;

import models.User;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class News implements Serializable {
    private static final long serialVersionUID = 1L;
    private String title;
    private String content;
    private Date postDate;
    private boolean isPinned;
    private List<Comment> comments = new ArrayList<>(); 

    public News(String title, String content, boolean isPinned) {
        this.title = title;
        this.content = content;
        this.isPinned = isPinned;
        this.postDate = new Date();
    }

    public void addComment(User author, String text) {
        comments.add(new Comment(author, text));
    }

    public List<Comment> getComments() {
        return comments;
    }

    public String getTitle() { return title; }
    public String getContent() { return content; }
    public Date getPostDate() { return postDate; }
    public boolean isPinned() { return isPinned; }

    @Override
    public String toString() {
        return "Title: " + title + "\nContent: " + content;
    }

    public static class Comment implements Serializable {
        private static final long serialVersionUID = 1L;
        private User author;
        private String text;
        private Date date;

        public Comment(User author, String text) {
            this.author = author;
            this.text = text;
            this.date = new Date();
        }

        public User getAuthor() { return author; }
        public String getText() { return text; }
        public Date getDate() { return date; }

        @Override
        public String toString() {
            return author.getName() + ": " + text + " (" + date + ")";
        }
    }
}