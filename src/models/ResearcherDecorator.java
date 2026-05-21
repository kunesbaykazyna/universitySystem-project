package models;

import exceptions.NonResearcherException;
import utils.*;

import java.io.Serializable;
import java.util.*;

public class ResearcherDecorator extends UserDecorator implements Serializable {
    private static final long serialVersionUID = 1L;

    public ResearcherDecorator(UserComponent user) {
        super(user);
    }

    // декорируем юзера как ресерчера
    private User getUser() {
        return (User) decoratedUser;
    }

    public int calculateHIndex() {
        return getUser().calculateHIndex();
    }

    public void addPaper(ResearchPaper paper) {
        getUser().addPaper(paper);
    }

    public void printPapers(Comparator<ResearchPaper> comparator) {
        getUser().printPapers(comparator);
    }

    public void addProject(ResearchProject project) {
        getUser().addResearchProject(project);
    }

    public void joinProject(ResearchProject project) throws NonResearcherException {
        project.addParticipant(this);
    }

    public void publishToJournal(UniversityJournal journal, ResearchPaper paper) {
        addPaper(paper);
        journal.publishPaper(paper);
    }
    
    public List<ResearchPaper> getPublishedPapers() {
        return getUser().getPublishedPapers();
    }

    public List<ResearchProject> getProjects() {
        return getUser().getResearchProjects();
    }
}