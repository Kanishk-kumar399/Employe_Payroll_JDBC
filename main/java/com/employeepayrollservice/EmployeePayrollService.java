package com.employeepayrollservice;
import java.util.List;

public class EmployeePayrollService {
public EmployeePayrollJDBCService employeePayrollDBService;
	
	public EmployeePayrollService() {
		this.employeePayrollDBService = new EmployeePayrollJDBCService();
	}
	
	public List<EmployeePayrollData> readEmployeePayrollData() throws EmployeePayrollJDBCException{
		return this.employeePayrollDBService.readData();
	}
}
