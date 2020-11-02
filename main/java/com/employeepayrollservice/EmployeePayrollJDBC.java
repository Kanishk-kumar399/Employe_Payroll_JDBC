package com.employeepayrollservice;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
public class EmployeePayrollJDBC 
{
	private static final String jdbcURL = "jdbc:mysql://localhost:3306/payroll_service?useSSL=false";
	private static final String userName = "root";
	private static final String password = "Kanishk111*";

	public static void main(String[] args)
	{
	/*UC1*/
		Connection connection;
		try 
		{
			Class.forName("com.mysql.cj.jdbc.Driver");
			System.out.println("Driver Loaded!");
		} 
		catch (ClassNotFoundException e) {
			throw new IllegalStateException("Cannot Find The driver in the classpath",e);
		}
		listDrivers();
		try {
			System.out.println("Connecting to database: " + jdbcURL);
			connection = DriverManager.getConnection(jdbcURL, userName, password);
			System.out.println("Connection is Successfull!!! " + connection);
		} 
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private static void listDrivers() {
		Enumeration<Driver> driverList = DriverManager.getDrivers();
		while (driverList.hasMoreElements()) {
			Driver driverClass = driverList.nextElement();
			System.out.println("Drivers: " + driverClass.getClass().getName());
		}
	}
} 