package models;

import enumerations.Faculty;

import enumerations.GraduateLevel;
import exceptions.LowHIndexException;
import utils.ResearchPaper;

import java.util.ArrayList;
import java.util.List;

import core.LocalizationManager;

public class GraduatedStudent extends Student {
    private static final long serialVersionUID = 1L;

    private ResearcherDecorator supervisor;        
    private GraduateLevel level;
    private List<ResearchPaper> diplomaProjects; 

    public GraduatedStudent(String userId, String name, String password, String login,
                            int yos, Faculty faculty, ResearcherDecorator supervisor, GraduateLevel level) 
                            throws LowHIndexException {
        super(userId, name, password, login, yos, faculty);

        if (supervisor.calculateHIndex() < 3) {
        	throw new LowHIndexException(LocalizationManager.getString("err_low_hindex", 
                    supervisor.getName(), 
                    supervisor.calculateHIndex()));
        }

        this.supervisor = supervisor;
        this.level = level;
        this.diplomaProjects = new ArrayList<>();
    }

    public ResearcherDecorator getSupervisor() {
        return supervisor;
    }

    public GraduateLevel getLevel() {
        return level;
    }

    public List<ResearchPaper> getDiplomaProjects() {
        return diplomaProjects;
    }

    public void addDiplomaProject(ResearchPaper paper) {
        diplomaProjects.add(paper);
    }

    @Override
    public String toString() {
    	return LocalizationManager.getString("graduated_student_info", 
                getUserId(), 
                getName(), 
                level, 
                supervisor.getName(), 
                diplomaProjects.size());
    }
}