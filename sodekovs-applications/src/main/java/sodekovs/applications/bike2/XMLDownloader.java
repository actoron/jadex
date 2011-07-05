package sodekovs.applications.bike2;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.bind.JAXBException;

import sodekovs.applications.bike2.xml.XMLHandler;
import sodekovs.applications.bike2.xml.urls.URLEntry;
import sodekovs.applications.bike2.xml.urls.URLs;

/**
 * XML Downloader class which starts a {@link TimerTask} for every given URL from which XML data should be loaded periodically. The loaded XML data is being inserted into a MySQL database.
 * 
 * @author Thomas Preisler
 */
public class XMLDownloader {

	/** The delay for starting the download task in ms */
	private static final int TIME_TO_START = 50;

	/** The path to the XML file containing the urls */
	private static final String URL_FILE = "urls.xml";

	/**
	 * Main method, starts a {@link DownloadFileTask} for every URL specified in {@link XMLDownloader#URLS}.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			URLs urls = (URLs) XMLHandler.retrieveFromXML(URLs.class, URL_FILE);

			for (URLEntry entry : urls.getEntries()) {
				Timer timer = new Timer();
				// start the timer task for every given url
				timer.schedule(new DownloadFileTask(entry.getCity(), new URL(entry.getUrl())), TIME_TO_START, entry.getInterval());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
}