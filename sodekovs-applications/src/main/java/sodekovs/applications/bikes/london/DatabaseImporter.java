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
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import au.com.bytecode.opencsv.CSVReader;

/**
 * @author thomas
 * 
 */
public class DatabaseImporter {

	private static final String LOG_FILE_PATH = "importer.log";

	private Logger logger = null;

	private Connection connection = null;

	public DatabaseImporter() {
		this.connection = DatabaseConnection.getConnection();

		this.logger = Logger.getLogger("LondonImporter");
		this.logger.setLevel(Level.ALL);
		this.logger.setUseParentHandlers(false);

		FileHandler fh;
		try {
			fh = new FileHandler(LOG_FILE_PATH, true);
			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);

			logger.addHandler(fh);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void log(String msg) {
		this.logger.log(Level.INFO, msg);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DatabaseImporter importer = new DatabaseImporter();
		importer.log("London Database Import started.");
		importer.log("-------------------------------");

		for (String arg : args) {
			File csvFile = new File(arg);
			if (csvFile.exists()) {
				importer.log("Started parsing " + csvFile);

				try {
					BufferedReader br = new BufferedReader(new FileReader(csvFile));
					CSVReader csvReader = new CSVReader(br);
					List<String[]> lines = csvReader.readAll();

					importer.log("Parsed " + lines.size() + " lines");
					for (String[] line : lines) {
						importer.insertLine(line);
					}

					csvReader.close();
					br.close();

					importer.log("Finished parsing " + csvFile);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}

		importer.log("-------------------------------");
		importer.log("London Database Import finished.");
	}

	private void insertLine(String[] line) throws IOException {
		int i = 0;
		if (line.length == 11) {
			i++;
		}

		// Journey Id
		Integer journeyId = Integer.parseInt(line[i++]);
		// Bike Id
		Integer bikeId = Integer.parseInt(line[i++]);
		// Start Date
		String startDateString = getMySQLDateString(line[i++]);
		// Start Time
		String startTimeString = line[i++];
		if (startTimeString.length() == 5) {
			startTimeString += ":00";
		}
		Timestamp start = Timestamp.valueOf(startDateString + " " + startTimeString);
		// End Date
		String endDateString = getMySQLDateString(line[i++]);
		// End Time
		String endTimeString = line[i++];
		if (endTimeString.length() == 5) {
			endTimeString += ":00";
		}
		Timestamp end = Timestamp.valueOf(endDateString + " " + endTimeString);
		// Start Station
		String startStation = line[i++];
		// Start Station Id
		Integer startStationId = Integer.parseInt(line[i++]);
		// End Station
		String endStation = line[i++];
		// End Station Id
		Integer endStationId = Integer.parseInt(line[i++]);

		try {
			if (connection != null) {
				PreparedStatement statement = connection
						.prepareStatement("INSERT INTO JOURNEYDETAILS(journeyId, bikeId, start, end, startStation, startStationId, endStation, endStationId) VALUES (?, ?, ?, ?, ?, ?, ? ,?)");
				statement.setInt(1, journeyId);
				statement.setInt(2, bikeId);
				statement.setTimestamp(3, start);
				statement.setTimestamp(4, end);
				statement.setString(5, startStation);
				statement.setInt(6, startStationId);
				statement.setString(7, endStation);
				statement.setInt(8, endStationId);

				if (statement.executeUpdate() != 0) {
					log(statement.toString());
				}
			}
		} catch (NumberFormatException e) {
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
}
