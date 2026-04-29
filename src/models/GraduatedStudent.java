package models;

import enumerations.Faculty;
import enumerations.GraduateLevel;

public class GraduatedStudent extends Student{
	private static final long serialVersionUID = 1L;
	private Teacher superVisor;
	public GraduateLevel level;
	public GraduatedStudent(String userId, String name, String password, String login, int yos,
			Faculty faculty,Teacher superVisor,GraduateLevel level) {
		super(userId, name, password, login, yos, faculty);
		this.level=level;
		this.superVisor=superVisor;
	}
	public Teacher getSuperVisor() {
		return superVisor;
	}
	
	

}
