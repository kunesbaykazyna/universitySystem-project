package utils;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import core.LocalizationManager;
import enumerations.UrgencyLevel;
import models.Student;
import models.Teacher;

public class Complaint implements Serializable{
	private static final long serialVersionUID = 1L;
    private String text;
    private UrgencyLevel urgency;
    private Date date;
    private Teacher teacher;
    private Student student;

    public Complaint(Teacher teacher,Student student, String text, UrgencyLevel urgency) {
        this.text = text;
        this.urgency = urgency;
        this.teacher=teacher;
        this.student=student;
        this.date = new Date();
    }


	public String getText() {
        return text;
    }

    public UrgencyLevel getUrgency() {
        return urgency;
    }

    public Date getDate() {
        return date;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Complaint)) return false;

        Complaint complaint = (Complaint) o;

        if (!Objects.equals(text, complaint.text)) return false;
        if (urgency != complaint.urgency) return false;
        if (!Objects.equals(date, complaint.date)) return false;
        if (!Objects.equals(teacher, complaint.teacher)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, urgency, date, teacher);
    }

    @Override
    public String toString() {
        return LocalizationManager.getString("complaint_info", teacher.getName(), urgency, text);
    }

	public Student getStudent() {
		return student;
	}
}
