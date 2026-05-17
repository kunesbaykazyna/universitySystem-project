package models;

import java.util.Objects;

import core.LocalizationManager;

public class Employee extends User implements Comparable<Employee> {
	private static final long serialVersionUID = 1L;
	private double salary;
	public Employee(String userId, String name, String password, String login,double salary) {
		super(userId, name, password, login);
		this.setSalary(salary);
	}
	public double getSalary() {
		return salary;
	}
	public void setSalary(double salary) {
		this.salary = salary;
	}

	@Override
	public String toString() {
		return LocalizationManager.getString("employee_info", getUserId(), getName(), getSalary());
	}
	
	@Override 
	public boolean equals(Object o) {
		if (!(o instanceof Employee)) return false;
		if(this==o) return true;
		Employee employee=(Employee) o;
		return Double.compare(employee.salary, salary) == 0 &&
		           Objects.equals(getUserId(), employee.getUserId()) &&
		           Objects.equals(getLogin(), employee.getLogin()) &&
		           Objects.equals(getPassword(), employee.getPassword());
	}
	
	@Override
	public int hashCode() {
	    return Objects.hash(super.hashCode(), salary, getUserId(), getLogin(),getPassword());
	}
	
	@Override
    public int compareTo(Employee other) {
        return Double.compare(this.salary, other.salary);
    }
}
