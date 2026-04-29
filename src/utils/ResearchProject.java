package utils;

import java.io.Serializable;
import java.util.List;
import models.ResearcherDecorator;

public class ResearchProject implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String topic;
    private List<ResearchPaper> publishedPapers;
    private List<ResearcherDecorator> participants;

    public ResearchProject() {}

    public void addPaper(ResearchPaper paper) {
    }

    public void addParticipant(ResearcherDecorator researcher) {
    }

	public String getTopic() {
		return topic;
	}

	public List<ResearchPaper> getPublishedPapers() {
		return publishedPapers;
	}

	public List<ResearcherDecorator> getParticipants() {
		return participants;
	}
}
