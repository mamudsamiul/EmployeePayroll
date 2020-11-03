package com.capgemini.employeepayroll;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class EmpPayrollService {
	private List<EmployeePayrollData> employeePayrollList;

	public EmpPayrollService() {
	}

	public EmpPayrollService(List<EmployeePayrollData> employeePayrollList) {
		this.employeePayrollList = employeePayrollList;
	}

	private void readEmployeePayrollData(Scanner consoleInputReader) {
		System.out.print("Enter Employee ID: ");
		int id = consoleInputReader.nextInt();
		System.out.print("Enter Employee Name: ");
		String name = consoleInputReader.next();
		System.out.print("Enter Employee Salary: ");
		double salary = consoleInputReader.nextDouble();
		employeePayrollList.add(new EmployeePayrollData(id, name, salary));
	}

	private void writeEmployeePayrollData(Scanner consoleInputReader) {
		System.out.println("\nWriting Employee Payroll Data to Console\n" + employeePayrollList);
	}

	public static void main(String[] args) {
		List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
		EmpPayrollService employeePayrollService = new EmpPayrollService(employeePayrollList);
		Scanner consoleInputReader = new Scanner(System.in);
		employeePayrollService.readEmployeePayrollData(consoleInputReader);
		employeePayrollService.writeEmployeePayrollData(consoleInputReader);
	}
}
