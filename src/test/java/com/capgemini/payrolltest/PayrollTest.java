package com.capgemini.payrolltest;

import org.junit.Test;

import com.capgemini.employeepayroll.EmpPayrollService;
import com.capgemini.employeepayroll.EmployeePayrollData;

import junit.framework.Assert;

import static org.junit.Assert.*;

import java.util.Arrays;

public class PayrollTest {
	@Test
	public void given3EmployeesWhenWrittenToFileShouldMatchNumberOfEmployeeEntries() {
		EmployeePayrollData[] arrayOfEmployees = { new EmployeePayrollData(1, "Samiul Mamud", 10000.0),
				new EmployeePayrollData(2, "Rahul Ghosh", 11000.0),
				new EmployeePayrollData(3, "Dwipayan Das", 12000.0) };
		EmpPayrollService empPayrollService;
		empPayrollService = new EmpPayrollService(Arrays.asList(arrayOfEmployees));
		empPayrollService.writeEmpPayrollData(EmpPayrollService.IOService.FILE_IO);
		empPayrollService.printData(EmpPayrollService.IOService.FILE_IO);
		Assert.assertEquals(3, empPayrollService.countEntries(EmpPayrollService.IOService.FILE_IO));
	}
}
