package sodekovs.applications.bikes.datafetcher;

import java.io.File;
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

	/** The path to the XML file containing the urls */
	private static final String URL_FILE ="/sodekovs-applications/src/main/java/sodekovs/applications/bikes/datafetcher/urls.xml";
	
	private static final String LOG_FILE = "Datafetcher.log";

	/**
	 * Main method, starts a {@link DownloadFileTask} for every URL specified in {@link XMLDownloader#URLS}.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Logger logger = Logger.getLogger("Datafetcher");
			logger.setLevel(Level.ALL);
			
			FileHandler fh = new FileHandler(LOG_FILE, true);
			
			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);
			
			logger.addHandler(fh);

			logger.log(Level.INFO, "XML Bike Data Fetcher started.");
			String filePath = new File("..").getCanonicalPath() + URL_FILE;
			URLs urls = (URLs) XMLHandler.retrieveFromXML(URLs.class, filePath);
			logger.log(Level.INFO, "URLs retrieved from " + filePath);
			
			for (URLEntry entry : urls.getEntries()) {
				Timer timer = new Timer();
				// start the timer task for every given url
				timer.schedule(new DownloadFileTask(entry.getCity(), new URL(entry.getUrl()), logger), TIME_TO_START, entry.getInterval());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}