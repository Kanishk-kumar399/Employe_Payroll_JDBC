package com.employeepayrollservice;
import java.time.LocalDate;
import java.util.List;

public class EmployeePayrollService
{
public EmployeePayrollJDBCService employeePayrollDBService;
private List<EmployeePayrollData> employeePayrollList;
	public EmployeePayrollService() {
		this.employeePayrollDBService = EmployeePayrollJDBCService.getInstance();
	}
	
	public List<EmployeePayrollData> readEmployeePayrollData() throws EmployeePayrollJDBCException{
		this.employeePayrollList = this.employeePayrollDBService.readData();
		return this.employeePayrollList;
	}
	public void updateEmployeeSalary(String name,double salary) throws EmployeePayrollJDBCException
	{
		int result=new EmployeePayrollJDBCService().updateEmployeePayrollDataUsingPreparedStatement(name,salary);
		if(result==0)
			return;
		EmployeePayrollData employeePayrollData=this.getEmployeePayrollData(name);
		if(employeePayrollData !=null)
			employeePayrollData.setSalary(salary);
	}

	public EmployeePayrollData getEmployeePayrollData(String name) {
		return this.employeePayrollList.stream()
				.filter(employeePayrollObject->employeePayrollObject.getName().equals(name))
				.findFirst().orElse(null);
	}

	public boolean checkEmployeePayrollInSyncWithDB(String name) throws EmployeePayrollJDBCException {
		List<EmployeePayrollData> employeePayrollDataList=new EmployeePayrollJDBCService().getEmployeePayrollDataFromDB(name);
		return employeePayrollDataList.get(0).equals(getEmployeePayrollData(name));
	}
	public List<EmployeePayrollData> getEmployeePayrollDataByStartDate(LocalDate startDate, LocalDate endDate)throws EmployeePayrollJDBCException {
		return this.employeePayrollDBService.getEmployeePayrollDataByStartingDate(startDate, endDate);
	}
}
