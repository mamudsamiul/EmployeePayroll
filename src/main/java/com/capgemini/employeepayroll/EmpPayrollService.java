package com.capgemini.employeepayroll;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class EmpPayrollService {

	public enum IOService {
		CONSOLE_IO, FILE_IO, DB_IO, REST_IO
	}

	private List<EmployeePayrollData> employeePayrollList;
	private EmployeePayrollDBService employeePayrollDBService;

	public EmpPayrollService() {
		employeePayrollDBService = EmployeePayrollDBService.getInstance();
	}

	public EmpPayrollService(List<EmployeePayrollData> employeePayrollList) {
		this();
		this.employeePayrollList = employeePayrollList;
	}

	public void readEmployeePayrollData(IOService ioService) {
		if (ioService.equals(IOService.CONSOLE_IO)) {
			Scanner consoleInputReader = new Scanner(System.in);
			System.out.print("Enter Employee ID: ");
			int id = consoleInputReader.nextInt();
			System.out.print("Enter Employee Name: ");
			String name = consoleInputReader.next();
			System.out.print("Enter Employee Salary: ");
			double salary = consoleInputReader.nextDouble();
			consoleInputReader.close();
			employeePayrollList.add(new EmployeePayrollData(id, name, salary));
		} else if (ioService.equals(IOService.FILE_IO)) {
			new EmployeePayrollFileIOService().readData();
		}
	}

	public List<EmployeePayrollData> readEmpPayrollData(IOService ioService) throws EmpPayrollException {
		if (ioService.equals(IOService.DB_IO))
			employeePayrollList = employeePayrollDBService.readData();
		return employeePayrollList;
	}

	public void writeEmpPayrollData(IOService ioService) {
		if (ioService.equals(IOService.CONSOLE_IO))
			System.out.println("\nWriting Payroll to Console\n" + employeePayrollList);
		else if (ioService.equals(IOService.FILE_IO))
			new EmployeePayrollFileIOService().writeData(employeePayrollList);

	}

	public void printData(IOService ioService) {
		if (ioService.equals(IOService.FILE_IO))
			new EmployeePayrollFileIOService().printData();
	}

	public long countEntries(IOService ioService) {
		if (ioService.equals(IOService.FILE_IO))
			return new EmployeePayrollFileIOService().countEntries();
		return 0;
	}

	public void updateEmployeeSalary(String name, double salary) throws EmpPayrollException {
		int result = employeePayrollDBService.updateEmployeeData(name, salary);
		if (result == 0)
			return;
		EmployeePayrollData employeePayrollData = this.getEmployeePayrollData(name);
		if (employeePayrollData != null)
			employeePayrollData.setSalary(salary);
	}

	private EmployeePayrollData getEmployeePayrollData(String name) {
		return this.employeePayrollList.stream().filter(employee -> employee.getName().contentEquals(name)).findFirst()
				.orElse(null);

	}

	public boolean checkEmployeePayrollInSyncWithDB(String name) throws EmpPayrollException {
		EmployeePayrollData employeePayrollData = employeePayrollDBService.getEmployeePayrollData(name);
		return employeePayrollData.getSalary().equals(getEmployeePayrollData(name).getSalary());
	}

	public List<EmployeePayrollData> getEmployeePayrollDataForDateRange(LocalDate startDate,
			LocalDate endDate)
			throws EmpPayrollException {
		return employeePayrollDBService.getEmployeePayrollDataForDateRange(startDate, endDate);
	}

	public double getSumByGender(IOService ioService, String c) throws EmpPayrollException {
		double sum = 0.0;
		if (ioService.equals(IOService.DB_IO))
			return employeePayrollDBService.getSumByGender(c);
		return sum;
	}

	public double getEmpDataGroupedByGender(IOService ioService, String column, String operation, String gender)
			throws EmpPayrollException {
		if (ioService.equals(IOService.DB_IO))
			return employeePayrollDBService.getEmpDataGroupedByGender(column, operation, gender);
		return 0.0;
	}

}