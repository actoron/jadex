package sodekovs.bikesharing.data;

import java.sql.Timestamp;

/**
 * Help object for the generation of the real data based simulation data. Encapsulates the information about one rental action.
 * 
 * @author thomas
 */
public class Rental {

	private String bikeId = null;

	private Timestamp start = null;

	private Timestamp end = null;

	private String startStation = null;

	private String endStation = null;

	private int weekday = -1;

	private String link = null;

	private String city = null;

	/**
	 * @return the bikeId
	 */
	public String getBikeId() {
		return bikeId;
	}

	/**
	 * @param bikeId
	 *            the bikeId to set
	 */
	public void setBikeId(String bikeId) {
		this.bikeId = bikeId;
	}

	/**
	 * @return the start
	 */
	public Timestamp getStart() {
		return start;
	}

	/**
	 * @param start
	 *            the start to set
	 */
	public void setStart(Timestamp start) {
		this.start = start;
	}

	/**
	 * @return the end
	 */
	public Timestamp getEnd() {
		return end;
	}

	/**
	 * @param end
	 *            the end to set
	 */
	public void setEnd(Timestamp end) {
		this.end = end;
	}

	/**
	 * @return the startStation
	 */
	public String getStartStation() {
		return startStation;
	}

	/**
	 * @param startStation
	 *            the startStation to set
	 */
	public void setStartStation(String startStation) {
		this.startStation = startStation;
	}

	/**
	 * @return the endStation
	 */
	public String getEndStation() {
		return endStation;
	}

	/**
	 * @param endStation
	 *            the endStation to set
	 */
	public void setEndStation(String endStation) {
		this.endStation = endStation;
	}

	/**
	 * @return the weekday
	 */
	public int getWeekday() {
		return weekday;
	}

	/**
	 * @param weekday
	 *            the weekday to set
	 */
	public void setWeekday(int weekday) {
		this.weekday = weekday;
	}

	/**
	 * @return the link
	 */
	public String getLink() {
		return link;
	}

	/**
	 * @param link
	 *            the link to set
	 */
	public void setLink(String link) {
		this.link = link;
	}

	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @param city
	 *            the city to set
	 */
	public void setCity(String city) {
		this.city = city;
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
		result = prime * result + ((bikeId == null) ? 0 : bikeId.hashCode());
		result = prime * result + ((city == null) ? 0 : city.hashCode());
		result = prime * result + ((end == null) ? 0 : end.hashCode());
		result = prime * result + ((endStation == null) ? 0 : endStation.hashCode());
		result = prime * result + ((link == null) ? 0 : link.hashCode());
		result = prime * result + ((start == null) ? 0 : start.hashCode());
		result = prime * result + ((startStation == null) ? 0 : startStation.hashCode());
		result = prime * result + weekday;
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
		Rental other = (Rental) obj;
		if (bikeId == null) {
			if (other.bikeId != null)
				return false;
		} else if (!bikeId.equals(other.bikeId))
			return false;
		if (city == null) {
			if (other.city != null)
				return false;
		} else if (!city.equals(other.city))
			return false;
		if (end == null) {
			if (other.end != null)
				return false;
		} else if (!end.equals(other.end))
			return false;
		if (endStation == null) {
			if (other.endStation != null)
				return false;
		} else if (!endStation.equals(other.endStation))
			return false;
		if (link == null) {
			if (other.link != null)
				return false;
		} else if (!link.equals(other.link))
			return false;
		if (start == null) {
			if (other.start != null)
				return false;
		} else if (!start.equals(other.start))
			return false;
		if (startStation == null) {
			if (other.startStation != null)
				return false;
		} else if (!startStation.equals(other.startStation))
			return false;
		if (weekday != other.weekday)
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
		return "Rental [bikeId=" + bikeId + ", start=" + start + ", end=" + end + ", startStation=" + startStation + ", endStation=" + endStation + ", weekday=" + weekday + ", link=" + link
				+ ", city=" + city + "]";
	}

	/**
	 * @param bikeId
	 * @param start
	 * @param end
	 * @param startStation
	 * @param endStation
	 * @param weekday
	 * @param link
	 * @param city
	 */
	public Rental(String bikeId, Timestamp start, Timestamp end, String startStation, String endStation, int weekday, String link, String city) {
		super();
		this.bikeId = bikeId;
		this.start = start;
		this.end = end;
		this.startStation = startStation;
		this.endStation = endStation;
		this.weekday = weekday;
		this.link = link;
		this.city = city;
	}

	/**
	 * 
	 */
	public Rental() {
		super();
	}
}