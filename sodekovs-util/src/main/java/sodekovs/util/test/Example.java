package sodekovs.util.test;

/**
 * 
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Random;

/**
 * @author Tom
 */
public class Example {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		// System.out.println(FileHandler.readFileAsString("E:\\Workspaces\\Vilenica-SVN\\Desktop\\Dissertation\\Text\\02_grundlagen.tex"));

		// RANDOM TEST
		Random random = new java.util.Random();
		double rand = random.nextDouble();
		ArrayList<Double> destProb = new ArrayList<Double>();
		destProb.add(0.12);
		destProb.add(0.30);
		destProb.add(0.05);
		destProb.add(0.27);
		destProb.add(0.26);

		System.out.println("rand is: +" + rand);

		double sum = 0.0;
		for (int i = 0; i < destProb.size(); i++) {

			sum += destProb.get(i);

			if (i + 1 < destProb.size()) {
				if (sum >= rand) {
					System.out.println("prob pos: " + i + " has won");
					break;
				}
			} else {
				System.out.println("#last bucket# prob pos: " + i + " has won");
			}			
		}

		// Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
		// Properties properties = new Properties();
		// properties.put("user", "user1");
		// properties.put("password", "user1");
		// // Connection connection = DriverManager.getConnection("jdbc:derby:c:/TEMP/tutorialsDB;create=true", properties);
		// Connection connection = DriverManager.getConnection("jdbc:derby:C:/Users/vilenica/MyDB;create=false", properties);
		//
		// createTableTestIfItDoesntExistYet(connection);
		// populateTableTestIfItHasNotBeenPopulatedYet(connection);
		// showContentsOfTableTest(connection);
		//
		// connection.close();
	}

	/**
	 * @param connection
	 * @throws SQLException
	 */
	private static void showContentsOfTableTest(Connection connection) throws SQLException {
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery("SELECT * FROM test");
		int columnCnt = resultSet.getMetaData().getColumnCount();
		boolean shouldCreateTable = true;
		while (resultSet.next() && shouldCreateTable) {
			for (int i = 1; i <= columnCnt; i++) {
				System.out.print(resultSet.getString(i) + " ");
			}
			System.out.println();
		}
		resultSet.close();
		statement.close();
	}

	private static void populateTableTestIfItHasNotBeenPopulatedYet(Connection connection) throws Exception {

		boolean shouldPopulateTable = true;
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM ante1");
		if (resultSet.next()) {
			shouldPopulateTable = resultSet.getInt(1) == 0;
		}
		resultSet.close();
		statement.close();

		if (shouldPopulateTable) {
			System.out.println("Populating Table test...");
			PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO ante1 VALUES (?,?)");
			String[] data = { "AAA", "BBB", "CCC", "DDD", "EEE" };
			for (int i = 0; i < data.length; i++) {
				preparedStatement.setInt(1, i);
				preparedStatement.setString(2, data[i]);
				preparedStatement.execute();
			}
			preparedStatement.close();
		}
	}

	private static void createTableTestIfItDoesntExistYet(Connection connection) throws Exception {
		ResultSet resultSet = connection.getMetaData().getTables("%", "%", "%", new String[] { "TABLE" });
		int columnCnt = resultSet.getMetaData().getColumnCount();
		boolean shouldCreateTable = true;
		while (resultSet.next() && shouldCreateTable) {
			if (resultSet.getString("TABLE_NAME").equalsIgnoreCase("ante1")) {
				shouldCreateTable = false;
			}
		}
		resultSet.close();
		if (shouldCreateTable) {
			System.out.println("Creating Table test...");
			Statement statement = connection.createStatement();
			statement.execute("create table ante1 (id int not null, data varchar(32))");
			statement.close();
		}
	}
}