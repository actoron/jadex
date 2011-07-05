package sodekovs.applications.bike2.xml.urls;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import sodekovs.applications.bike2.xml.XMLHandler;

/**
 * Object representation for the XML configuration file.
 * 
 * @author Thomas Preisler
 */
@XmlRootElement(name = "urls")
public class URLs {

	private List<URLEntry> entries = new ArrayList<URLEntry>();

	/**
	 * @return the entries
	 */
	@XmlElement(name = "entry")
	public List<URLEntry> getEntries() {
		return entries;
	}

	/**
	 * @param entries
	 *            the entries to set
	 */
	public void setEntries(List<URLEntry> entries) {
		this.entries = entries;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((entries == null) ? 0 : entries.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		URLs other = (URLs) obj;
		if (entries == null) {
			if (other.entries != null)
				return false;
		} else if (!entries.equals(other.entries))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final int maxLen = 10;
		return "URLs [" + (entries != null ? "entries=" + entries.subList(0, Math.min(entries.size(), maxLen)) : "") + "]";
	}

	public static void main(String[] args) {
		URLEntry london = new URLEntry();
		london.setCity("London");
		london.setInterval(180000);
		london.setUrl("http://www.tfl.gov.uk/tfl/syndication/feeds/cycle-hire/livecyclehireupdates.xml");

		URLEntry washington = new URLEntry();
		washington.setCity("Washington");
		washington.setInterval(180000);
		washington.setUrl("http://www.capitalbikeshare.com/stations/bikeStations.xml");

		URLs urls = new URLs();
		urls.getEntries().add(london);
		urls.getEntries().add(washington);

		try {
			XMLHandler.saveAsXML(URLs.class, urls, "urls.xml");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}
}