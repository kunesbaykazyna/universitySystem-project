package utils;

import java.util.Date;

import enumerations.UrgencyLevel;

public class Complaint {
	private String text;
    private UrgencyLevel urgency;
    private Date date;
    private String teacherId; 

    public Complaint(String text, UrgencyLevel urgency, String teacherId) {
        this.text = text;
        this.urgency = urgency;
        this.teacherId = teacherId;
        this.date = new Date();
    }

    @Override
    public String toString() {
        return "Жалоба от [" + teacherId + "] Срочность: " + urgency + "\nТекст: " + text;
    }

	public Date getDate() {
		return date;
	}
}
