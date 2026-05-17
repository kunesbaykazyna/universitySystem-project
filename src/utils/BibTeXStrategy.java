package utils;

public class BibTeXStrategy implements CitationStrategy {
    @SuppressWarnings("deprecation")
	@Override
    public String formatCitation(ResearchPaper paper) {
        return String.format("@article{%s,\n  author={%s},\n  title={%s},\n  journal={%s},\n  pages={%d},\n  year={%d},\n  doi={%s}\n}",
                paper.getDoi().replaceAll("[^a-zA-Z0-9]", "_"),
                paper.getAuthors(),
                paper.getTitle(),
                paper.getJournal(),
                paper.getPages(),
                paper.getDate().getYear() + 1900,
                paper.getDoi());
    }
}
