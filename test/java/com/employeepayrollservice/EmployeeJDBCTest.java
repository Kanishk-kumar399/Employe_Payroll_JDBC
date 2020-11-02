package com.employeepayrollservice;

import static org.junit.Assert.assertTrue;

import java.util.List;

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
}
