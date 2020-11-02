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
	private PreparedStatement preparedStatement;
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

	public List<EmployeePayrollData> readData() throws EmployeePayrollJDBCException{
		String sql = "select * from employee_payroll; ";
		List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			while (resultSet.next()) {
				int id = resultSet.getInt("id");
				String name = resultSet.getString("name");
				String gender = resultSet.getString("gender");
				double salary = resultSet.getDouble("salary");
				LocalDate startDate = resultSet.getDate("start").toLocalDate();
				employeePayrollList.add(new EmployeePayrollData(id, name, salary,gender, startDate));
			}
		} catch (SQLException e) {
			throw new EmployeePayrollJDBCException("Unable to get data.Please check table");
		}
	
	return employeePayrollList;
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
		if(this.preparedStatement==null) {
			this.prepareStatementForEmployeePayroll();
		}
		try {
			preparedStatement.setDouble(1, salary);
			preparedStatement.setString(2, name);
			int rowsAffected=preparedStatement.executeUpdate();
			return rowsAffected;
		}catch(SQLException e) {
			throw new EmployeePayrollJDBCException("Unable to use prepared statement");
		}
	}

	private void prepareStatementForEmployeePayroll() throws EmployeePayrollJDBCException {
		try {
			Connection connection=this.getConnection();
			String sql="update employee_payroll set salary=? where name=?";
			this.preparedStatement=connection.prepareStatement(sql);
		}catch (SQLException e) {
			throw new EmployeePayrollJDBCException("Unable to prepare statement");
		}
	}
	public List<EmployeePayrollData> getEmployeePayrollDataFromDB(String name) throws EmployeePayrollJDBCException 
	{
			String sql=String.format("select * from employee_payroll where name='%s'",name);
			List<EmployeePayrollData> employeePayrollList=new ArrayList<EmployeePayrollData>();
			try (Connection connection=this.getConnection()){
				Statement statement=connection.createStatement();
				ResultSet resultSet=statement.executeQuery(sql);
				while(resultSet.next()) {
					int id=resultSet.getInt("id");
					String objectname=resultSet.getString("name");
					String gender=resultSet.getString("gender");
					double salary=resultSet.getDouble("salary");
					LocalDate start=resultSet.getDate("start").toLocalDate();
					employeePayrollList.add(new EmployeePayrollData(id, objectname, salary, gender, start));
				}
				return employeePayrollList;
			} catch (SQLException e) {
				throw new EmployeePayrollJDBCException("Unable to get data from database");
			}
	}
}