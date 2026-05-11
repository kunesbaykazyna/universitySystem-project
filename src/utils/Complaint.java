package utils;

import java.util.Date;
import java.util.Objects;

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

    public String getText() {
        return text;
    }

    public UrgencyLevel getUrgency() {
        return urgency;
    }

    public Date getDate() {
        return date;
    }

    public String getTeacherId() {
        return teacherId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Complaint)) return false;

        Complaint complaint = (Complaint) o;

        if (!Objects.equals(text, complaint.text)) return false;
        if (urgency != complaint.urgency) return false;
        if (!Objects.equals(date, complaint.date)) return false;
        if (!Objects.equals(teacherId, complaint.teacherId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, urgency, date, teacherId);
    }

    @Override
    public String toString() {
        return "Жалоба от [" + teacherId + "], срочность: " + urgency + "\nТекст: " + text;
    }
}
