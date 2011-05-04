package haw.mmlab.production_line.common;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Helper class for accessing the logging level from the logging properties file.
 * 
 * @author thomas
 * 
 */
public class LoggingHelper {

	/**
	 * Returns the logging level as {@link String}.
	 * 
	 * @return the logging level
	 */
	public static String getLevel() {
		String level = "SEVERE";

		try {
			Properties properties = new Properties();
			properties.load(new FileInputStream("logging.properties"));

			level = properties.getProperty("logging.level");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return level;
	}
}