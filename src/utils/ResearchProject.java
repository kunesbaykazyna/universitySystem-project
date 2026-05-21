package utils;

import exceptions.NonResearcherException;
import models.ResearcherDecorator;
import models.UserComponent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ResearchProject implements Serializable {
    private static final long serialVersionUID = 1L;
    private String topic;
    private List<ResearchPaper> publishedPapers = new ArrayList<>();
    private List<ResearcherDecorator> participants = new ArrayList<>();

    public ResearchProject(String topic) {
        this.topic = topic;
    }

    public void addPaper(ResearchPaper paper) {
        if (paper != null) publishedPapers.add(paper);
    }

    public void addParticipant(UserComponent user) throws NonResearcherException {
        if (!(user instanceof ResearcherDecorator)) {
            throw new NonResearcherException("User " + user.getName() + " is not a researcher!");
        }
        ResearcherDecorator researcher = (ResearcherDecorator) user;
        if (!participants.contains(researcher)) participants.add(researcher);
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