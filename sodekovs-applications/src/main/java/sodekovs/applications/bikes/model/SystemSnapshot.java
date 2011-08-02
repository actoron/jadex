package sodekovs.applications.bikes.model;

import sodekovs.util.misc.TimeConverter;

/**
 * Model corresponds to the "stations" table of the MySQL DB for the bike sharing application. 
 * 
 * @author Vilenica
 *
 */

public class SystemSnapshot {

	private String cityName = null;
	private long timestamp = -1;
	/**
	 * Corresponds to the primary key.
	 */
	private int id =-1;
	
	
	/**
	 * @return the cityName
	 */
	public String getCityName() {
		return cityName;
	}
	/**
	 * @param cityName the cityName to set
	 */
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	/**
	 * @return the timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}
	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	public String toString(){
		return this.id + " - " + this.timestamp + "( " + TimeConverter.longTime2DateString(this.timestamp)+ " ) - " + this.cityName;
	}
	
}
