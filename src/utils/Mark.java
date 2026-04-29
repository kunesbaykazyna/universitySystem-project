package utils;

public class Mark {
	private double att1;
	private double att2;
	private double finalExam;
	
	public double getTotal() {
		return att1+att2+finalExam;
	}
	
	 public double convertToGpa(){
		 double total = getTotal();
		 if (total >= 95) return 4.0;
	     if (total >= 90) return 3.67;
	     if (total >= 85) return 3.33;
	     if (total >= 80) return 3.0;
	     if (total >= 75) return 2.67;
	     if (total >= 70) return 2.33;
	     if (total >= 65) return 2.0;
	     if (total >= 60) return 1.67;
	     if (total >= 55) return 1.33;
	     if (total >= 50) return 1.0;
	     return 0.0;
	 }
}
