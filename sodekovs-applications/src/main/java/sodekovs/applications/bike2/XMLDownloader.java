/**
 * 
 */
package sodekovs.applications.bike2;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;
import java.util.Timer;

/**
 * @author thomas
 * 
 */
public class XMLDownloader {

	/** All the URLs that should be polled */
	private static final String[] URLS = { "London,http://www.tfl.gov.uk/tfl/syndication/feeds/cycle-hire/livecyclehireupdates.xml",
			"Washington,http://www.capitalbikeshare.com/stations/bikeStations.xml" };

	/** The delay for starting the download task in ms */
	private static final int TIME_TO_START = 50;

	/** The timer interval (ms) in which the download task is repeated */
	private static final int DELAY_BETWEEN_POLLS = 180000;

	/**
	 * Main method, starts a {@link DownloadFileTask} for every URL specified in {@link XMLDownloader#URLS}.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			for (String urlString : URLS) {
				StringTokenizer tok = new StringTokenizer(urlString, ",");
				String city = tok.nextToken();
				URL url = new URL(tok.nextToken());

				Timer timer = new Timer();
				timer.schedule(new DownloadFileTask(city, url), TIME_TO_START, DELAY_BETWEEN_POLLS);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
}
