package com.capgemini.payrolltest;

import java.util.List;

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
}
