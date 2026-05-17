package utils;

public class PlainTextStrategy implements CitationStrategy {
    @Override
    public String formatCitation(ResearchPaper paper) {
        return String.format("%s. \"%s\". %s, %d pages, DOI: %s",
                paper.getAuthors(), paper.getTitle(), paper.getJournal(), paper.getPages(), paper.getDoi());
    }
}
