package com.employeepayrollservice;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	public Map<String, Double> performOperationByGender(String column,String operation) throws EmployeePayrollJDBCException {
		return this.employeePayrollDBService.performVariousOperations(column,operation);
	}

	public void addEmployeeToPayroll(String name, double salary, LocalDate startdate, String gender) throws EmployeePayrollJDBCException {
		employeePayrollList.add(employeePayrollDBService.addEmployeeToPayroll(name,salary,startdate,gender));
	}
	public void addEmployeeToPayrollWithThreads(List<EmployeePayrollData> employeePayrollDataList)
	{
		Map<Integer,Boolean> employeeAddditionStatus=new HashMap<>();
	}
	public EmployeePayrollData addNewEmployee(int id, String name, String gender, String phone_no, String address,Date date, double salary, String comp_name, int comp_id, String[] department, int[] dept_id) throws EmployeePayrollJDBCException{
		return EmployeePayrollJDBCService.getInstance().addNewEmployee
								(id, name, gender, phone_no, address, date, salary, comp_name, comp_id, department, dept_id);
	}
	public void removeEmployee(String name) throws EmployeePayrollJDBCException {
		if (!this.checkEmployeePayrollInSyncWithDB(name))
			throw new EmployeePayrollJDBCException("employee absent");
		EmployeePayrollJDBCService.getInstance().removeEmployee(name);
	}
}
