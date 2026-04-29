package utils;

import java.io.Serializable;
import java.util.Date;

public class News implements Serializable{
	private static final long serialVersionUID = 1L; 
	private String title;
    private String content;
    private Date postDate;
    private boolean isPinned;

    public News(String title, String content, boolean isPinned) {
        this.title = title;
        this.content = content;
        this.isPinned = isPinned;
        this.postDate = new Date();
    }
    
    @Override
    public String toString() {
        return "Title: " + title + "\nContent: " + content;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
	public Date getPostDate() {
		return postDate;
	}

	public boolean isPinned() {
		return isPinned;
	}
}
