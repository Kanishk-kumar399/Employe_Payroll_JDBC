package com.employeepayrollservice;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeePayrollJDBCService 
{
	private static EmployeePayrollJDBCService employeePayrollDBService;
	private PreparedStatement preparedStatementForUpdation;
	private PreparedStatement employeePayrollDataStatement;
	public EmployeePayrollJDBCService() {}

	public static EmployeePayrollJDBCService getInstance() {
		if(employeePayrollDBService==null) {
			employeePayrollDBService=new EmployeePayrollJDBCService();
		}
		return employeePayrollDBService;
	}

	public Connection getConnection() throws EmployeePayrollJDBCException {
		String jdbcURL = "jdbc:mysql://localhost:3306/payroll_service?useSSL=false";
		String user = "root";
		String password = "Kanishk111*";
		Connection connection;
		System.out.println("Connecting to database: " + jdbcURL);
		try{
			connection = DriverManager.getConnection(jdbcURL, user, password);
			System.out.println("Connection is SuccessFull!!! " + connection);
			return connection;
		}
		catch(SQLException e)
		{
			throw new EmployeePayrollJDBCException("Unable to establish the connection");
		}
	}
	
	public List<EmployeePayrollData> readData() throws EmployeePayrollJDBCException
	{
		String sql = "select * from employee_payroll; ";
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			return this.getEmployeePayrollListFromResultset(resultSet);
		} catch (SQLException e) {
			throw new EmployeePayrollJDBCException("Unable to get data.Please check table");
		}
	}
	private List<EmployeePayrollData> getEmployeePayrollListFromResultset(ResultSet resultSet) throws EmployeePayrollJDBCException {
		List<EmployeePayrollData> employeePayrollList = new ArrayList<EmployeePayrollData>();
		try {
			while (resultSet.next()) {
				int id = resultSet.getInt("id");
				String objectname = resultSet.getString("name");
				String gender = resultSet.getString("gender");
				double salary = resultSet.getDouble("salary");
				LocalDate start = resultSet.getDate("start").toLocalDate();
				employeePayrollList.add(new EmployeePayrollData(id, objectname, salary, gender, start));
				}
				return employeePayrollList;
			}catch (SQLException e) {
			throw new EmployeePayrollJDBCException("Unable to use the result set");
			}
	}
	public int updateEmployeeDataUsingStatement(String name, double salary) throws EmployeePayrollJDBCException {
		String sql=String.format("update employee_payroll set salary=%.2f where name='%s'",salary,name);
		try (Connection connection=this.getConnection()){
			Statement statement=connection.createStatement();
			int rowsAffected=statement.executeUpdate(sql);
			return rowsAffected;
		} catch (SQLException e) {
			throw new EmployeePayrollJDBCException("Unable To update data in database");
		}
	}

	public int updateEmployeePayrollDataUsingPreparedStatement(String name, double salary) throws EmployeePayrollJDBCException {
		if(this.preparedStatementForUpdation==null) {
			this.prepareStatementForEmployeePayroll();
		}
		try {
			preparedStatementForUpdation.setDouble(1, salary);
			preparedStatementForUpdation.setString(2, name);
			int rowsAffected=preparedStatementForUpdation.executeUpdate();
			return rowsAffected;
		}catch(SQLException e) {
			throw new EmployeePayrollJDBCException("Unable to use prepared statement");
		}
	}

	private void prepareStatementForEmployeePayroll() throws EmployeePayrollJDBCException {
		try {
			Connection connection=this.getConnection();
			String sql="update employee_payroll set salary=? where name=?";
			this.preparedStatementForUpdation=connection.prepareStatement(sql);
		}catch (SQLException e) {
			throw new EmployeePayrollJDBCException("Unable to create prepare statement");
		}
	}
	public List<EmployeePayrollData> getEmployeePayrollDataFromDB(String name) throws EmployeePayrollJDBCException 
	{
		if (this.employeePayrollDataStatement == null) {
			this.prepareStatementForEmployeePayrollDataRetrieval();
		}
		try (Connection connection = this.getConnection()) {
			this.employeePayrollDataStatement.setString(1, name);
			ResultSet resultSet = employeePayrollDataStatement.executeQuery();
			return this.getEmployeePayrollListFromResultset(resultSet);
		} catch (SQLException e) {
			throw new EmployeePayrollJDBCException("Unable to read");
		}
	}
	private void prepareStatementForEmployeePayrollDataRetrieval() throws EmployeePayrollJDBCException {
		try {
			Connection connection = this.getConnection();
			String sql = "select * from employee_payroll where name=?";
			this.employeePayrollDataStatement = connection.prepareStatement(sql);
		} catch (SQLException e) {
			throw new EmployeePayrollJDBCException("Unable to create prepare statement");
		}
	}
	public List<EmployeePayrollData> getEmployeePayrollDataByStartingDate(LocalDate startDate, LocalDate endDate)
			throws EmployeePayrollJDBCException {
		String sql = String.format("select * from employee_payroll where start between cast('%s' as date) and cast('%s' as date);",
				startDate.toString(), endDate.toString());
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			return this.getEmployeePayrollListFromResultset(resultSet);
		} catch (SQLException e) {
			throw new EmployeePayrollJDBCException("Connection Failed.");
		}
	}

	public Map<String, Double> performVariousOperations(String column, String operation) throws EmployeePayrollJDBCException{
		String sql=String.format("select gender , %s(%s) from employee_payroll group by gender;" , operation , column);
		Map<String,Double> mapValues = new HashMap<>();
		try(Connection connection = this.getConnection()) {
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next())
			{
				mapValues.put(resultSet.getString(1), resultSet.getDouble(2));
			}
		}
		catch (SQLException e) {
			throw new EmployeePayrollJDBCException("Connection Failed.");
		}
		return mapValues;
	}

	public EmployeePayrollData addEmployeeToPayrollUC7(String name, double salary, LocalDate startdate, String gender) throws EmployeePayrollJDBCException {
		int employeeId=-1;
		EmployeePayrollData employeePayrollData=null;
		String sql=String.format("INSERT INTO employee_payroll (name,gender,salary,start) VALUES ('%s','%s','%s','%s')",name,gender,salary,Date.valueOf(startdate));
		try(Connection connection=this.getConnection()){
			Statement statement=connection.createStatement();
			int rowsAffected=statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
			if(rowsAffected==1)
			{
				ResultSet resultSet=statement.getGeneratedKeys();
				if(resultSet.next()) employeeId=resultSet.getInt(1);
			}
			employeePayrollData=new EmployeePayrollData(employeeId, name, salary, gender, startdate);
		}
		catch (SQLException e) {
			throw new EmployeePayrollJDBCException("Could Not Add");}
		return employeePayrollData;
	}
	public EmployeePayrollData addEmployeeToPayroll(String name, double salary, LocalDate startdate, String gender) throws EmployeePayrollJDBCException {
		int employeeId=-1;
		Connection connection=null;
		EmployeePayrollData employeePayrollData=null;
		try
		{
			connection=this.getConnection();
		}
		catch (Exception e) {
			throw new EmployeePayrollJDBCException("Error");
		}
		try(Statement statement=connection.createStatement())
		{
		String sql=String.format("INSERT INTO employee_payroll (name,gender,salary,start) VALUES ('%s','%s','%s','%s')",name,gender,salary,Date.valueOf(startdate));
			int rowsAffected=statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
			if(rowsAffected==1)
			{
				ResultSet resultSet=statement.getGeneratedKeys();
				if(resultSet.next()) employeeId=resultSet.getInt(1);
			}
			employeePayrollData=new EmployeePayrollData(employeeId, name, salary, gender, startdate);
		}
		catch (SQLException e) {
			throw new EmployeePayrollJDBCException("Could Not Add");}
		try(Statement statement=connection.createStatement())
		{
			double deductions=salary*0.2;
			double taxablePay=salary-deductions;
			double tax=taxablePay*0.1;
			double netPay=salary-tax;
			String sql=String.format("INSERT INTO payroll_details (emp_id,basic_pay,deductions,taxable_pay,tax,net_pay) VALUES "
					+ "('%s','%s','%s','%s','%s','%s')",employeeId,salary,deductions,taxablePay,tax,netPay);
			int rowsAffected=statement.executeUpdate(sql);
			if(rowsAffected==1)
				employeePayrollData=new EmployeePayrollData(employeeId, name, salary, gender, startdate);
		} catch (SQLException e) {
			throw new EmployeePayrollJDBCException("Not Able to add");
		}
		return employeePayrollData;
	}
}