/**
 * 
 */
package sodekovs.applications.bikes.london;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.util.Scanner;
import java.util.StringTokenizer;

import com.csvreader.CsvReader;

/**
 * @author thomas
 * 
 */
public class DatabaseImporter {

	private static final String DB_URL = "jdbc:mysql://192.168.2.104:3306/london";

	private static final String DB_USER = "root";

	private static int count = 0;

	private static String password = null;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("London Database Import started.");
		System.out.println("-------------------------------");

		for (String arg : args) {
			File csvFile = new File(arg);

			System.out.println("Started parsing " + csvFile);
			if (csvFile.exists()) {
				try {
					BufferedReader br = new BufferedReader(new FileReader(csvFile));
					CsvReader csvReader = new CsvReader(br);
					csvReader.readHeaders();

					while (csvReader.readRecord()) {
						insertLine(csvReader);
					}

					// while ((line = br.readLine()) != null) {
					// if (!firstLine) {
					// insertLine(line);
					// } else {
					// firstLine = false;
					// }
					// }
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			System.out.println("Finished parsing " + csvFile);
		}

		System.out.println("\nInserted " + count + " lines to the Database.");
		System.out.println("-------------------------------");
		System.out.println("London Database Import finished.");

		// Double d = 40321.0;
		// String s = String.valueOf(d);
		// System.out.println(s);
	}

	private static void insertLine(CsvReader csvReader) {
		try {
			// Journey Id
			Integer journeyId = Integer.parseInt(csvReader.get("Journey Id"));
			// Bike Id
			Integer bikeId = Integer.parseInt(csvReader.get("Bike Id"));
			// Start Date
			String startDateString = getMySQLDateString(csvReader.get("Start Date"));
			Date startDate = Date.valueOf(startDateString);
			// Start Time
			String startTimeString = csvReader.get("Start Time");
			if (startTimeString.length() == 5) {
				startTimeString += ":00";
			}
			Time startTime = Time.valueOf(startTimeString);
			// End Date
			String endDateString = getMySQLDateString(csvReader.get("End Date"));
			Date endDate = Date.valueOf(endDateString);
			// End Time
			String endTimeString = csvReader.get("End Time");
			if (endTimeString.length() == 5) {
				endTimeString += ":00";
			}
			Time endTime = Time.valueOf(endTimeString);
			// Start Station
			String startStation = csvReader.get("Start Station");
			// Start Station Id
			Integer startStationId = Integer.parseInt(csvReader.get("Start Station Id"));
			// End Station
			String endStation = csvReader.get("End Station");
			// End Station Id
			Integer endStationId = Integer.parseInt(csvReader.get("End Station Id"));

			Connection connection = getDatabaseConnection();
			if (connection != null) {
				PreparedStatement statement = connection
						.prepareStatement("INSERT INTO JOURNEYDETAILS(journeyId, bikeId, startDate, startTime, endDate, endTime, startStation, startStationId, endStation, endStationId) VALUES (?, ?, ?, ?, ?, ?, ? ,?, ? ,?)");
				statement.setInt(1, journeyId);
				statement.setInt(2, bikeId);
				statement.setDate(3, startDate);
				statement.setTime(4, startTime);
				statement.setDate(5, endDate);
				statement.setTime(6, endTime);
				statement.setString(7, startStation);
				statement.setInt(8, startStationId);
				statement.setString(9, endStation);
				statement.setInt(10, endStationId);

				if (statement.executeUpdate() != 0) {
					count++;
					System.out.println(statement);
				}
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	private static String getMySQLDateString(String date) {
		StringTokenizer tok = new StringTokenizer(date, "/");

		String day = tok.nextToken();
		String month = tok.nextToken();
		String year = tok.nextToken();

		String result = year + "-" + month + "-" + day;
		return result;
	}

	private static Connection getDatabaseConnection() {
		Connection connection = null;

		if (password == null) {
			System.out.println("Please insert the password to access " + DB_URL + " as " + DB_USER);
			Scanner s = new Scanner(System.in);
			password = s.next();
		}

		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			connection = DriverManager.getConnection(DB_URL, DB_USER, password);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return connection;
	}
}
