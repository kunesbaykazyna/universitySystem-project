package utils;

import java.io.Serializable;
import java.util.Date;
import java.text.SimpleDateFormat;

public class ResearchPaper implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String title;
    private String authors;
    private String journal;
    private int pages;
    private Date date;
    private int citations;
    private String doi;

    public ResearchPaper(String title2, String authors2, String journal2, int pages2, Date date2, int citations2, String doi) {
        this.authors = authors2;
        this.citations = citations2;
        this.title = title2;
        this.journal = journal2;
        this.pages = pages2;
        this.date = date2;
        this.doi = doi;
    }

    public String getCitation(CitationStrategy strategy) {
        return strategy.formatCitation(this);
    }

    public String getTitle() { return title; }
    public String getAuthors() { return authors; }
    public int getPages() { return pages; }
    public String getJournal() { return journal; }
    public String getDoi() { return doi; }
    public Date getDate() { return date; }
    public int getCitations() { return citations; }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return title + " by " + authors + ", " + journal + ", " + sdf.format(date) + ", citations: " + citations;
    }
}