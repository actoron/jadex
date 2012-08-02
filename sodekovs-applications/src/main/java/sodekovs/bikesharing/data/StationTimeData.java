/**
 * 
 */
package sodekovs.bikesharing.data;

import java.util.Date;

/**
 * Temp object for handling result data from the database.
 * 
 * @author Thomas Preisler
 */
public class StationTimeData {

	private String stationId = null;
	private Integer nbBikes = null;
	private Integer nbEmptyDocks = null;
	private Date date = null;
	private Integer weekday = null;
	private Integer hour = null;

	/**
	 * @return the stationId
	 */
	public String getStationId() {
		return stationId;
	}

	/**
	 * @param stationId
	 *            the stationId to set
	 */
	public void setStationId(String stationId) {
		this.stationId = stationId;
	}

	/**
	 * @return the nbBikes
	 */
	public Integer getNbBikes() {
		return nbBikes;
	}

	/**
	 * @param nbBikes
	 *            the nbBikes to set
	 */
	public void setNbBikes(Integer nbBikes) {
		this.nbBikes = nbBikes;
	}

	/**
	 * @return the nbEmptyDocks
	 */
	public Integer getNbEmptyDocks() {
		return nbEmptyDocks;
	}

	/**
	 * @param nbEmptyDocks
	 *            the nbEmptyDocks to set
	 */
	public void setNbEmptyDocks(Integer nbEmptyDocks) {
		this.nbEmptyDocks = nbEmptyDocks;
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @param date
	 *            the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * @return the weekday
	 */
	public Integer getWeekday() {
		return weekday;
	}

	/**
	 * @param weekday
	 *            the weekday to set
	 */
	public void setWeekday(Integer weekday) {
		this.weekday = weekday;
	}

	/**
	 * @return the hour
	 */
	public Integer getHour() {
		return hour;
	}

	/**
	 * @param hour
	 *            the hour to set
	 */
	public void setHour(Integer hour) {
		this.hour = hour;
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
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((hour == null) ? 0 : hour.hashCode());
		result = prime * result + ((nbBikes == null) ? 0 : nbBikes.hashCode());
		result = prime * result + ((nbEmptyDocks == null) ? 0 : nbEmptyDocks.hashCode());
		result = prime * result + ((stationId == null) ? 0 : stationId.hashCode());
		result = prime * result + ((weekday == null) ? 0 : weekday.hashCode());
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
		StationTimeData other = (StationTimeData) obj;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (hour == null) {
			if (other.hour != null)
				return false;
		} else if (!hour.equals(other.hour))
			return false;
		if (nbBikes == null) {
			if (other.nbBikes != null)
				return false;
		} else if (!nbBikes.equals(other.nbBikes))
			return false;
		if (nbEmptyDocks == null) {
			if (other.nbEmptyDocks != null)
				return false;
		} else if (!nbEmptyDocks.equals(other.nbEmptyDocks))
			return false;
		if (stationId == null) {
			if (other.stationId != null)
				return false;
		} else if (!stationId.equals(other.stationId))
			return false;
		if (weekday == null) {
			if (other.weekday != null)
				return false;
		} else if (!weekday.equals(other.weekday))
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
		return "StationTimeData [" + (stationId != null ? "stationId=" + stationId + ", " : "") + (nbBikes != null ? "nbBikes=" + nbBikes + ", " : "")
				+ (nbEmptyDocks != null ? "nbEmptyDocks=" + nbEmptyDocks + ", " : "") + (date != null ? "date=" + date + ", " : "") + (weekday != null ? "weekday=" + weekday + ", " : "")
				+ (hour != null ? "hour=" + hour : "") + "]";
	}
}
