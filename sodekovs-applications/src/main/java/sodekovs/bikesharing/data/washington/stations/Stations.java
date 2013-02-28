package sodekovs.bikesharing.data.washington.stations;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This class represents a {@link List} of {@link Station}s to a given timestamp.
 * 
 * @author Thomas Preisler
 */
@XmlRootElement(name = "stations")
public class Stations {

	/** Unix timestamp */
	private Long lastUpate = 0L;

	/** The version */
	private String version = null;

	/** All the stations */
	private List<Station> stations = new ArrayList<Station>();

	/**
	 * @return the lastUpate
	 */
	@XmlAttribute(name = "lastUpdate")
	public Long getLastUpate() {
		return lastUpate;
	}

	/**
	 * @param lastUpate
	 *            the lastUpate to set
	 */
	public void setLastUpate(Long lastUpate) {
		this.lastUpate = lastUpate;
	}

	/**
	 * @return the version
	 */
	@XmlAttribute(name = "version")
	public String getVersion() {
		return version;
	}

	/**
	 * @param version
	 *            the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @return the stations
	 */
	@XmlElement(name = "station")
	public List<Station> getStations() {
		return stations;
	}

	/**
	 * @param stations
	 *            the stations to set
	 */
	public void setStations(List<Station> stations) {
		this.stations = stations;
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
		result = prime * result + ((lastUpate == null) ? 0 : lastUpate.hashCode());
		result = prime * result + ((stations == null) ? 0 : stations.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
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
		Stations other = (Stations) obj;
		if (lastUpate == null) {
			if (other.lastUpate != null)
				return false;
		} else if (!lastUpate.equals(other.lastUpate))
			return false;
		if (stations == null) {
			if (other.stations != null)
				return false;
		} else if (!stations.equals(other.stations))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
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
		return "Stations [" + (lastUpate != null ? "lastUpate=" + lastUpate + ", " : "") + (version != null ? "version=" + version + ", " : "")
				+ (stations != null ? "stations=" + stations.subList(0, Math.min(stations.size(), maxLen)) : "") + "]";
	}
}