package models;

import java.util.*;
import research.*;

public class ResearcherDecorator extends UserDecorator {
    private List<ResearchPaper> publishedPapers = new ArrayList<>();
    private List<ResearchProject> projects = new ArrayList<>();

    public ResearcherDecorator(UserComponent user) {
        super(user);
    }

    public double calculateHIndex() {
        double hIndex=0;
        return hIndex;
    }

    public void addPaper(ResearchPaper paper) {
        publishedPapers.add(paper);
    }
    
    

    public void printPapers(Comparator<ResearchPaper> comp) {
        publishedPapers.sort(comp);
        publishedPapers.forEach(System.out::println);
    }
}