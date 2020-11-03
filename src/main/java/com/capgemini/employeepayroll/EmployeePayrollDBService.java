package com.capgemini.employeepayroll;

import java.sql.Connection;
import java.sql.Date;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EmployeePayrollDBService {

	private static EmployeePayrollDBService employeePayrollDBService;
	private PreparedStatement employeePayrollDataStatement;

	private EmployeePayrollDBService() {
	}

	public static EmployeePayrollDBService getInstance() {
		if (employeePayrollDBService == null)
			employeePayrollDBService = new EmployeePayrollDBService();
		return employeePayrollDBService;
	}

	private Connection getConnection() throws SQLException {
		String jdbcURL = "jdbc:mysql://localhost:3306/payroll_service?allowPublicKeyRetrieval=true&&useSSL=false";
		String userName = "root";
		String password = "admin";
		Connection connection;
		connection = DriverManager.getConnection(jdbcURL, userName, password);
		return connection;
	}

	public List<EmployeePayrollData> readData() throws EmpPayrollException {
		String sql = "SELECT * FROM employee_payroll;";
		List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(sql);
			employeePayrollList = getEmployeePayrollData(result);
		} catch (SQLException e) {
			throw new EmpPayrollException(EmpPayrollException.ExceptionType.CONNECTION_ERROR, e.getMessage());
		}
		return employeePayrollList;
	}

	public int updateEmployeeData(String name, double salary) throws EmpPayrollException {
		return this.updateEmployeeDataUsingStatement(name, salary);
	}

	private int updateEmployeeDataUsingStatement(String name, double salary) throws EmpPayrollException {
		String sql = String.format("update employee_payroll set basic_pay = %.2f where name ='%s';", salary, name);
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			return statement.executeUpdate(sql);
		} catch (SQLException e) {
			throw new EmpPayrollException(EmpPayrollException.ExceptionType.CONNECTION_ERROR, e.getMessage());
		}
	}

	public List<EmployeePayrollData> getEmployeePayrollDataFromDB(String name) throws EmpPayrollException {
		List<EmployeePayrollData> employeePayrollList = null;
		if (this.employeePayrollDataStatement == null)
			this.prepareStatementForEmployeeData();
		try {
			employeePayrollDataStatement.setString(1, name);
			ResultSet resultSet = employeePayrollDataStatement.executeQuery();
			employeePayrollList = this.getEmployeePayrollData(resultSet);
		} catch (SQLException e) {
			throw new EmpPayrollException(EmpPayrollException.ExceptionType.CONNECTION_ERROR, e.getMessage());
		}
		return employeePayrollList;
	}

	public EmployeePayrollData getEmployeePayrollData(String name) throws EmpPayrollException {
		List<EmployeePayrollData> employeePayrollList = this.readData();
		EmployeePayrollData employeeData = employeePayrollList.stream()
				.filter(employee -> employee.getName().contentEquals(name)).findFirst().orElse(null);
		return employeeData;

	}

	private void prepareStatementForEmployeeData() throws EmpPayrollException {
		try {
			Connection connection = this.getConnection();
			String sql = "SELECT * FROM employee_payroll WHERE name = ?";
			employeePayrollDataStatement = connection.prepareStatement(sql);
		} catch (SQLException e) {
			throw new EmpPayrollException(EmpPayrollException.ExceptionType.CONNECTION_ERROR, e.getMessage());
		}
	}

	private List<EmployeePayrollData> getEmployeePayrollData(ResultSet result) throws EmpPayrollException {
		List<EmployeePayrollData> employeePayrollList = new ArrayList<EmployeePayrollData>();
		try {
			while (result.next()) {
				int id = result.getInt("id");
				String name = result.getString("name");
				double salary = result.getDouble("basic_pay");
				LocalDate startDate = result.getDate("start").toLocalDate();
				String gender = result.getString("gender");
				employeePayrollList.add(new EmployeePayrollData(id, name, salary, startDate, gender));
			}
		} catch (SQLException e) {
			throw new EmpPayrollException(EmpPayrollException.ExceptionType.CONNECTION_ERROR, e.getMessage());
		}
		return employeePayrollList;
	}

	public List<EmployeePayrollData> getEmployeePayrollDataForDateRange(LocalDate startDate, LocalDate endDate)
			throws EmpPayrollException {
		String sql = String.format("SELECT * FROM employee_payroll WHERE start BETWEEN '%s' AND '%s';",
				Date.valueOf(startDate), Date.valueOf(endDate));
		List<EmployeePayrollData> employeePayrollList = new ArrayList<EmployeePayrollData>();
		try (Connection connection = getConnection()) {
			employeePayrollDataStatement = connection.prepareStatement(sql);
			ResultSet resultSet = employeePayrollDataStatement.executeQuery();
			while (resultSet.next()) {
				employeePayrollList.add(new EmployeePayrollData(resultSet.getInt("id"), resultSet.getString("name"),
						resultSet.getDouble("basic_pay"), resultSet.getDate("start").toLocalDate()));
			}
		} catch (SQLException e) {
			throw new EmpPayrollException(EmpPayrollException.ExceptionType.INCORRECT_INFO, e.getMessage());
		}
		return employeePayrollList;
	}

	public double getSumByGender(String c) throws EmpPayrollException {
		List<EmployeePayrollData> employeePayrollList = this.readData();
		double sum = 0.0;
		List<EmployeePayrollData> sortByGenderList = employeePayrollList.stream()
				.filter(employee -> employee.getGender().equals(c)).collect(Collectors.toList());
		sum = sortByGenderList.stream().map(employee -> employee.getSalary()).reduce(0.0, Double::sum);
		return sum;
	}

	public double getEmpDataGroupedByGender(String column, String operation, String gender) throws EmpPayrollException {

		Map<String, Double> sumByGenderMap = new HashMap<>();
		String sql = String.format("SELECT gender, %s(%s) FROM employee_payroll GROUP BY gender;", operation, column);
		try (Connection connection = getConnection()) {
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				sumByGenderMap.put(resultSet.getString(1), resultSet.getDouble(2));
			}
		} catch (SQLException e) {
			throw new EmpPayrollException(EmpPayrollException.ExceptionType.INCORRECT_INFO, e.getMessage());
		}
		if (gender.equals("M")) {
			return sumByGenderMap.get("M");
		}
		return sumByGenderMap.get("F");
	}

	public Map<String, Double> getAvgSalaryByGender() throws EmpPayrollException {
		String sql = "SELECT gender, AVG(basic_pay) as avg_salary FROM employee_payroll GROUP BY gender;";
		Map<String, Double> genderToAvgSalaryMap = new HashMap<>();
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(sql);
			while (result.next()) {
				String gender = result.getString("gender");
				double salary = result.getDouble("avg_salary");
				genderToAvgSalaryMap.put(gender, salary);
			}

		} catch (SQLException e) {
			throw new EmpPayrollException(EmpPayrollException.ExceptionType.INCORRECT_INFO, e.getMessage());
		}
		return genderToAvgSalaryMap;
	}

	public EmployeePayrollData addEmpToPayroll(String name, double salary, LocalDate start, String gender)
			throws EmpPayrollException {
		int id = -1;
		EmployeePayrollData employeePayrollData = null;
		String sql = String.format(
				"INSERT INTO employee_payroll(name, basic_pay, start, gender, deductions,taxable_pay,tax,net_pay) VALUES('%s', '%s', '%s', '%s',0,0,0,0);", name,
				salary, Date.valueOf(start), gender);
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			int rowAffected = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
			if (rowAffected == 1) {
				ResultSet resultSet = statement.getGeneratedKeys();
				if (resultSet.next())
					id = resultSet.getInt(1);
			}
			employeePayrollData = new EmployeePayrollData(id, name, salary, start, gender);
		} catch (SQLException e) {
			throw new EmpPayrollException(EmpPayrollException.ExceptionType.INCORRECT_INFO, e.getMessage());
		}
		return employeePayrollData;
	}
}