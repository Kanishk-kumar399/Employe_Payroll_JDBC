package com.employeepayrollservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.Date;
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
		employeePayrollService.updateEmployeeSalary("Terisa",30000000.00);
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
    //UC7 and UC8 and refactor
    @Test
    public void givenNewEmployee_WhenAdded_ShouldGiveProperResult() throws EmployeePayrollJDBCException {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.readEmployeePayrollData();
		employeePayrollService.addEmployeeToPayroll("Mark",50000000.00,LocalDate.now(),"M");
		boolean result=employeePayrollService.checkEmployeePayrollInSyncWithDB("Mark");
		Assert.assertTrue(result);
    }
    //UC9 and UC10
    @Test
    public void givennewEmployeeDetails_addItInEveryTableShouldGiveProperResult() throws EmployeePayrollJDBCException{
    	EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.readEmployeePayrollData();
		Date date = Date.valueOf("2020-11-03");
		boolean result;
		String[] departments = {"SalesAndBusiness", "Marketing"};
		int[] dept_id = {01, 02};
		EmployeePayrollData employeePayrollData = employeePayrollService.addNewEmployee
					(1001, "Kanishk", "M", "88845649", "Varnasi 5154985", date, 3000000,
							"Capgemini", 111, departments, dept_id );
		boolean results=employeePayrollService.checkEmployeePayrollInSyncWithDB("Kanishk");
		Assert.assertTrue(results);
    }
    //UC12
    @Test
	public void givenNameWhenDeletedShouldGetDeletedFromDatabase() throws EmployeePayrollJDBCException {
			EmployeePayrollService employeePayrollService = new EmployeePayrollService();
			employeePayrollService.removeEmployee("Ram");
			boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB("Kanishk");
			assertFalse(result);
	}
}
