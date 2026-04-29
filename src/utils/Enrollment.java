package utils;
import enumerations.*;
import models.*;

public class Enrollment {
	public Course course;
	public Student students;
	public Semester semester;
	private Mark mark;
	
	public void setMark(Mark mark) {
		this.mark=mark;
	}
	
	public Mark getMark() {
		return mark;
	}
	
		
}
