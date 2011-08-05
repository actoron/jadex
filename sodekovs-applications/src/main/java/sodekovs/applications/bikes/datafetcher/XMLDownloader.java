package sodekovs.applications.bikes.datafetcher;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.xml.bind.JAXBException;

import sodekovs.applications.bikes.datafetcher.brisbane.DownloadBrisbaneTask;
import sodekovs.applications.bikes.datafetcher.database.DatabaseConnection;
import sodekovs.applications.bikes.datafetcher.rennes.DownloadRennesTask;
import sodekovs.applications.bikes.datafetcher.xml.XMLHandler;
import sodekovs.applications.bikes.datafetcher.xml.urls.URLEntry;
import sodekovs.applications.bikes.datafetcher.xml.urls.URLs;

/**
 * XML Downloader class which starts a {@link TimerTask} for every given URL from which XML data should be loaded periodically. The loaded XML data is being inserted into a MySQL database.
 * 
 * @author Thomas Preisler
 */
public class XMLDownloader {

	/** The delay for starting the download task in ms */
	private static final int TIME_TO_START = 50;

	/**
	 * Main method, starts a {@link DownloadURLTask} for every URL specified in {@link XMLDownloader#URLS}.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length == 3) {
			try {
				String urlFilePath = args[0];
				String logFilePath = args[1];
				DatabaseConnection.DB_FILE = args[2];

				Logger logger = Logger.getLogger("Datafetcher");
				logger.setLevel(Level.ALL);

				FileHandler fh = new FileHandler(logFilePath, true);

				SimpleFormatter formatter = new SimpleFormatter();
				fh.setFormatter(formatter);

				logger.addHandler(fh);

				logger.log(Level.INFO, "XML Bike Data Fetcher started.");
				URLs urls = (URLs) XMLHandler.retrieveFromXML(URLs.class, urlFilePath);
				logger.log(Level.INFO, "URLs retrieved from " + urlFilePath);

				for (URLEntry entry : urls.getEntries()) {
					Timer timer = new Timer();
					// start the timer task for every given url
					timer.schedule(new DownloadURLTask(entry.getCity(), new URL(entry.getUrl()), logger), TIME_TO_START, entry.getInterval());
				}

				// start the download task for brisbane
				Timer timerBrisbane = new Timer();
				timerBrisbane.schedule(new DownloadBrisbaneTask(logger, "Brisbane"), TIME_TO_START, 180000);

				// start the download task for rennes
				Timer timerRennes = new Timer();
				timerRennes.schedule(new DownloadRennesTask(logger, "Rennes"), TIME_TO_START, 180000);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (JAXBException e) {
				e.printStackTrace();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.err.println("Error! Arguments have to be the path to the URL file, the path to the logging file and the path to the database properties file.");
		}
	}
}