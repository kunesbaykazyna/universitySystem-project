package utils;

import java.io.Serializable;
import java.util.Objects;

import core.LocalizationManager;

public class Mark implements Serializable{
	private static final long serialVersionUID = 1L;
	private double att1;
    private double att2;
    private double finalExam;

    public Mark() {
    }

    public Mark(double att1, double att2, double finalExam) {
        this.att1 = att1;
        this.att2 = att2;
        this.finalExam = finalExam;
    }

    public double getTotal() {
        return att1 + att2 + finalExam;
    }

    public double convertToGpa() {
        double total = getTotal();

        if (total >= 95) {
            return 4.0;
        }
        if (total >= 90) {
            return 3.67;
        }
        if (total >= 85) {
            return 3.33;
        }
        if (total >= 80) {
            return 3.0;
        }
        if (total >= 75) {
            return 2.67;
        }
        if (total >= 70) {
            return 2.33;
        }
        if (total >= 65) {
            return 2.0;
        }
        if (total >= 60) {
            return 1.67;
        }
        if (total >= 55) {
            return 1.33;
        }
        if (total >= 50) {
            return 1.0;
        }

        return 0.0;
    }

    public double getAtt1() {
        return att1;
    }

    public void setAtt1(double att1) {
        this.att1 = att1;
    }

    public double getAtt2() {
        return att2;
    }

    public void setAtt2(double att2) {
        this.att2 = att2;
    }

    public double getFinalExam() {
        return finalExam;
    }

    public void setFinalExam(double finalExam) {
        this.finalExam = finalExam;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Mark)) return false;

        Mark mark = (Mark) o;

        if (Double.compare(mark.att1, att1) != 0) return false;
        if (Double.compare(mark.att2, att2) != 0) return false;
        if (Double.compare(mark.finalExam, finalExam) != 0) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(att1, att2, finalExam);
    }

    @Override
    public String toString() {
        return LocalizationManager.getString("mark_info", att1, att2, finalExam, getTotal());
    }
}
