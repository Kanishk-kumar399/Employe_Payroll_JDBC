package com.employeepayrollservice;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
}