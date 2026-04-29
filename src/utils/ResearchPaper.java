package utils;

import java.io.Serializable;
import java.util.Date;

public class ResearchPaper implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String title;
    private String authors;
    private String journal;
    private int pages;
    private Date date;
    private int citations;
    private String doi;

    public ResearchPaper() {}


    public String getCitation(CitationStrategy strategy) {
        return strategy.formatCitation(this);
    }

	public String getTitle() {
		return title;
	}

	public String getAuthors() {
		return authors;
	}

	public int getPages() {
		return pages;
	}

	public String getJournal() {
		return journal;
	}

	public String getDoi() {
		return doi;
	}
	public Date getDate() {
		return date;
	}
	public int getCitations() {
		return citations;
	}
}

