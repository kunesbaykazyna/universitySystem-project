package utils;

import models.Student;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class StudentOrganization implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private Student head;
    private List<Student> members = new ArrayList<>();

    public StudentOrganization(String name, Student head) {
        this.name = name;
        this.head = head;
        members.add(head);
    }

    public void setHead(Student newHead) {
        if (!members.contains(newHead)) members.add(newHead);
        this.head = newHead;
    }

    public void addMember(Student student) {
        if (!members.contains(student)) members.add(student);
    }

    public void removeMember(Student student) {
        members.remove(student);
        if (student.equals(head) && !members.isEmpty()) {
            head = members.get(0);
        }
    }

    @Override
    public String toString() {
        return name + " (Head: " + head.getName() + ", Members: " + members.size() + ")";
    }
    
    public String getName() { 
    	return name; 
    }
    public Student getHead() { 
    	return head; 
    }
    public List<Student> getMembers() {
    	return members; 
    }
}