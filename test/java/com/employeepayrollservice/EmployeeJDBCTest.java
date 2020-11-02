package com.employeepayrollservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class EmployeeJDBCTest
{
    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }
    @Test
    //UC2
    public void givenEmployeePayrollInDB_WhenRetrieved_ShouldMatchEmployeeCount() throws EmployeePayrollJDBCException
    {
    	List<EmployeePayrollData> employeePayrollData;
    	EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollData =employeePayrollService.readEmployeePayrollData();
		Assert.assertEquals(3, employeePayrollData.size());
    }
    @Test
    //UC3
    public void givenEmployeePayrollInDB_WhenUpdated_ShouldSyncWithDB() throws EmployeePayrollJDBCException
    {
    	List<EmployeePayrollData> employeePayrollData;
    	EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollData =employeePayrollService.readEmployeePayrollData();
		employeePayrollService.updateEmployeeSalary("Terisa",300000.00);
		boolean result=employeePayrollService.checkEmployeePayrollInSyncWithDB("Terisa");
		Assert.assertTrue(result);
    }
    @Test
    //UC4
    public void givenEmployeePayrollInDB_WhenUpdatedUsingPreparedStatement_ShouldSyncWithDB() throws EmployeePayrollJDBCException
    {
    	List<EmployeePayrollData> employeePayrollData;
    	EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollData =employeePayrollService.readEmployeePayrollData();
		employeePayrollService.updateEmployeeSalary("Terisa",300000.00);
		boolean result=employeePayrollService.checkEmployeePayrollInSyncWithDB("Terisa");
		Assert.assertTrue(result);
    }
    @Test
    //UC5
    public void givenEmployeePayrollDataWhenRetrieved_BasedOn_StartingDateShouldGiveProperResult() throws EmployeePayrollJDBCException {
			EmployeePayrollService employeePayrollService = new EmployeePayrollService();
			employeePayrollService.readEmployeePayrollData();
			LocalDate startDate = LocalDate.parse("2018-01-31");
			LocalDate endDate = LocalDate.parse("2019-01-31");
			List<EmployeePayrollData> matchingRecords = employeePayrollService
					.getEmployeePayrollDataByStartDate(startDate, endDate);
			Assert.assertEquals(matchingRecords.get(0),employeePayrollService.getEmployeePayrollData("Terisa"));
	}
    @Test
    //UC6
    public void givenEmployeePerformed_VariousOperations_ShouldGiveProperResult() throws EmployeePayrollJDBCException {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.readEmployeePayrollData();
		Map<String, Double> averageSalaryByGender=employeePayrollService.performOperationByGender("salary","MAX");
		assertEquals(300000.0,averageSalaryByGender.get("F"), 0.0);
}
}
