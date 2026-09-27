package ems;

public class EmployeeUtils {
    public static double roundSalary(double salaryValue) {
        return Math.round(salaryValue * 100.0) / 100.0;
    }
}
