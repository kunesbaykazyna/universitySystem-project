package models;

import exceptions.NonResearcherException;
import utils.News;
import utils.ResearchPaper;
import utils.ResearchProject;
import java.io.Serializable;
import java.util.*;

import data.Database;

public class ResearcherDecorator extends UserDecorator implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private List<ResearchPaper> publishedPapers = new ArrayList<>();
    private List<ResearchProject> projects = new ArrayList<>();

    public ResearcherDecorator(UserComponent user) {
        super(user);
    }

    public int calculateHIndex() {
        if (publishedPapers.isEmpty()) return 0;
        List<Integer> citations = new ArrayList<>();
        for (ResearchPaper p : publishedPapers) citations.add(p.getCitations());
        citations.sort(Collections.reverseOrder());
        int h = 0;
        for (int i = 0; i < citations.size(); i++) {
            if (citations.get(i) >= i + 1) h = i + 1;
            else break;
        }
        return h;
    }

    public void joinProject(ResearchProject project) throws NonResearcherException {
        project.addParticipant(this);
    }

//    public void addPaper(ResearchPaper paper) {
//        publishedPapers.add(paper);
//        News news = new News("Research", getName() + " published a new paper: " + paper.getTitle(), true);
//        Database.getInstance().getNews().add(news);
//    }
    public void addPaper(ResearchPaper paper) {
        publishedPapers.add(paper);
       
        News news = new News("Research", getName() + " published a new paper: " + paper.getTitle(), true);
        Database.getInstance().getNews().add(news);
        Database.getInstance().announceTopResearcher();
        Database.getInstance().save();
    }

    public void printPapers(Comparator<ResearchPaper> comparator) {
        publishedPapers.sort(comparator);
        publishedPapers.forEach(System.out::println);
    }

    public List<ResearchPaper> getPublishedPapers() {
        return publishedPapers;
    }

    public List<ResearchProject> getProjects() {
        return projects;
    }

    public void addProject(ResearchProject project) {
        if (project != null && !projects.contains(project)) projects.add(project);
    }
}