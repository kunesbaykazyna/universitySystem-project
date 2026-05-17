package utils;

import java.io.Serializable;
import java.util.Objects;

import core.LocalizationManager;
import enumerations.Semester;
import models.Student;


public class Enrollment implements Serializable {
	private static final long serialVersionUID = 1L;
	private Course course;
    private Student student;
    private Semester semester;
    private Mark mark;
    private String status = "PENDING";

    public Enrollment(Course course, Student student, Semester semester) {
        this.course = course;
        this.student = student;
        this.semester = semester;
    }

    public void approve() {
        if (!"PENDING".equals(status)) {
            System.out.println(LocalizationManager.getString("err_approve_only_pending"));
            return;
        }
        this.status = "APPROVED";
    }

    public void reject() {
        if (!"PENDING".equals(status)) {
            System.out.println(LocalizationManager.getString("err_reject_only_pending"));
            return;
        }
        this.status = "REJECTED";
    }


    public void setMark(Mark mark) {
    	if ("APPROVED".equals(status)) {
    	    this.mark = mark;
    	} else {
            System.out.println(LocalizationManager.getString("err_mark_only_approved"));
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Enrollment)) return false;

        Enrollment that = (Enrollment) o;

        if (!Objects.equals(course, that.course)) return false;
        if (!Objects.equals(student, that.student)) return false;
        if (semester != that.semester) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(course, student, semester);
    }

    @Override
    public String toString() {
    	return LocalizationManager.getString("enrollment_info", 
                course, 
                student, 
                semester, 
                mark, 
                status);    }
    
    public Mark getMark() {
        return mark;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

	public Student getStudent() {
		return student;
	}
	
	public Course getCourse() {
		return course;
	}

}
