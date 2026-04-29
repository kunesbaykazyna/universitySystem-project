package models;

public class Employee extends User{
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

}
