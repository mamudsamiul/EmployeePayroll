package com.capgemini.payrolltest;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.capgemini.employeepayroll.EmpPayrollException;
import com.capgemini.employeepayroll.EmpPayrollService;
import com.capgemini.employeepayroll.EmpPayrollService.IOService;
import com.capgemini.employeepayroll.EmployeePayrollData;

import junit.framework.Assert;

public class EmployeePayrollDBTest {
	@Test
	public void givenEmployeePayrollDB_shouldReturnCount() throws EmpPayrollException {
		EmpPayrollService empPayRollService = new EmpPayrollService();
		List<EmployeePayrollData> empPayrollList = empPayRollService.readEmpPayrollData(IOService.DB_IO);
		Assert.assertEquals(4, empPayrollList.size());
	}

	@Test
	public void givenNewSalary_whenUpdatedShouldMatch() throws EmpPayrollException {
		EmpPayrollService empPayRollService = new EmpPayrollService();
		List<EmployeePayrollData> empPayrollList = empPayRollService.readEmpPayrollData(IOService.DB_IO);
		empPayRollService.updateEmployeeSalary("Bill", 150.00);
		boolean result = empPayRollService.checkEmployeePayrollInSyncWithDB("Bill");
		Assert.assertTrue(result);
	}

	@Test
	public void givenDateRangeWhenRetrieved_ShouldReturnEmpCount() throws EmpPayrollException {
		EmpPayrollService empPayRollService = new EmpPayrollService();
		LocalDate startDate = LocalDate.of(2018, 01, 01);
		LocalDate endDate = LocalDate.now();
		List<EmployeePayrollData> empPayrollList = empPayRollService.getEmployeePayrollDataForDateRange(startDate,
				endDate);
		Assert.assertEquals(4, empPayrollList.size());
	}

	@Test
	public void givenDBFindSumOfSalaryOfMale_shouldReturnSum() throws EmpPayrollException {
		EmpPayrollService empPayRollService = new EmpPayrollService();
		double sum = empPayRollService.getSumByGender(IOService.DB_IO, "M");
		double sum1 = empPayRollService.getEmpDataGroupedByGender(IOService.DB_IO, "basic_pay", "SUM", "M");
		Assert.assertTrue(sum == sum1);
	}

	@Test
	public void givenDBFindSumOfSalaryOfFemale_shouldReturnSum() throws EmpPayrollException {
		EmpPayrollService empPayRollService = new EmpPayrollService();
		double sum = empPayRollService.getSumByGender(IOService.DB_IO, "F");
		double sum1 = empPayRollService.getEmpDataGroupedByGender(IOService.DB_IO, "basic_pay", "SUM", "F");
		Assert.assertTrue(sum == sum1);
	}

	@Test
	public void givenDBFindAvgSalary_shouldReturnSum() throws EmpPayrollException {
		EmpPayrollService empPayRollService = new EmpPayrollService();
		empPayRollService.readEmpPayrollData(IOService.DB_IO);
		Map<String, Double> avgSalaryByGender = empPayRollService.readAvgSalary(IOService.DB_IO);
		Assert.assertTrue(avgSalaryByGender.get("M").equals(175.0) && avgSalaryByGender.get("F").equals(100.0));
	}

	@Test
	public void givenNewEmployee_WhenAdded_ShouldSyncWithDB() throws EmpPayrollException {
		EmpPayrollService empPayRollService = new EmpPayrollService();
		empPayRollService.readEmpPayrollData(IOService.DB_IO);
		empPayRollService.addEmpToPayroll("Jack", 300.0, LocalDate.now(), "M");
		boolean result = empPayRollService.checkEmployeePayrollInSyncWithDB("Jack");
		Assert.assertTrue(result);
	}
}
