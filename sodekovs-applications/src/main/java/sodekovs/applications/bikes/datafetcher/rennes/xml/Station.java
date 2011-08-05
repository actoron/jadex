/**
 * 
 */
package sodekovs.applications.bikes.datafetcher.rennes.xml;

import javax.xml.bind.annotation.XmlElement;

/**
 * XML Representation for a Station.
 * 
 * @author Thomas Preisler
 */
public class Station {

	private Integer number = null;

	private String name = null;

	private String address = null;

	private Integer state = null;

	private Double latitude = null;

	private Double longitude = null;

	private Integer slotsAvailable = null;

	private Integer bikesAvailable = null;

	private Integer pos = null;

	private String district = null;

	private String lastUpdate = null;

	/**
	 * @return the number
	 */
	@XmlElement(name = "number")
	public Integer getNumber() {
		return number;
	}

	/**
	 * @param number
	 *            the number to set
	 */
	public void setNumber(Integer number) {
		this.number = number;
	}

	/**
	 * @return the name
	 */
	@XmlElement(name = "name")
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the address
	 */
	@XmlElement(name = "address")
	public String getAddress() {
		return address;
	}

	/**
	 * @param address
	 *            the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return the state
	 */
	@XmlElement(name = "state")
	public Integer getState() {
		return state;
	}

	/**
	 * @param state
	 *            the state to set
	 */
	public void setState(Integer state) {
		this.state = state;
	}

	/**
	 * @return the latitude
	 */
	@XmlElement(name = "latitude")
	public Double getLatitude() {
		return latitude;
	}

	/**
	 * @param latitude
	 *            the latitude to set
	 */
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	/**
	 * @return the longitude
	 */
	@XmlElement(name = "longitude")
	public Double getLongitude() {
		return longitude;
	}

	/**
	 * @param longitude
	 *            the longitude to set
	 */
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	/**
	 * @return the slotsAvailable
	 */
	@XmlElement(name = "slotsavailable")
	public Integer getSlotsAvailable() {
		return slotsAvailable;
	}

	/**
	 * @param slotsAvailable
	 *            the slotsAvailable to set
	 */
	public void setSlotsAvailable(Integer slotsAvailable) {
		this.slotsAvailable = slotsAvailable;
	}

	/**
	 * @return the bikesAvailable
	 */
	@XmlElement(name = "bikesavailable")
	public Integer getBikesAvailable() {
		return bikesAvailable;
	}

	/**
	 * @param bikesAvailable
	 *            the bikesAvailable to set
	 */
	public void setBikesAvailable(Integer bikesAvailable) {
		this.bikesAvailable = bikesAvailable;
	}

	/**
	 * @return the pos
	 */
	@XmlElement(name = "pos")
	public Integer getPos() {
		return pos;
	}

	/**
	 * @param pos
	 *            the pos to set
	 */
	public void setPos(Integer pos) {
		this.pos = pos;
	}

	/**
	 * @return the district
	 */
	@XmlElement(name = "district")
	public String getDistrict() {
		return district;
	}

	/**
	 * @param district
	 *            the district to set
	 */
	public void setDistrict(String district) {
		this.district = district;
	}

	/**
	 * @return the lastUpdate
	 */
	@XmlElement(name = "lastupdate")
	public String getLastUpdate() {
		return lastUpdate;
	}

	/**
	 * @param lastUpdate
	 *            the lastUpdate to set
	 */
	public void setLastUpdate(String lastUpdate) {
		this.lastUpdate = lastUpdate;
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
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + ((bikesAvailable == null) ? 0 : bikesAvailable.hashCode());
		result = prime * result + ((district == null) ? 0 : district.hashCode());
		result = prime * result + ((lastUpdate == null) ? 0 : lastUpdate.hashCode());
		result = prime * result + ((latitude == null) ? 0 : latitude.hashCode());
		result = prime * result + ((longitude == null) ? 0 : longitude.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((number == null) ? 0 : number.hashCode());
		result = prime * result + ((pos == null) ? 0 : pos.hashCode());
		result = prime * result + ((slotsAvailable == null) ? 0 : slotsAvailable.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
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
		Station other = (Station) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (bikesAvailable == null) {
			if (other.bikesAvailable != null)
				return false;
		} else if (!bikesAvailable.equals(other.bikesAvailable))
			return false;
		if (district == null) {
			if (other.district != null)
				return false;
		} else if (!district.equals(other.district))
			return false;
		if (lastUpdate == null) {
			if (other.lastUpdate != null)
				return false;
		} else if (!lastUpdate.equals(other.lastUpdate))
			return false;
		if (latitude == null) {
			if (other.latitude != null)
				return false;
		} else if (!latitude.equals(other.latitude))
			return false;
		if (longitude == null) {
			if (other.longitude != null)
				return false;
		} else if (!longitude.equals(other.longitude))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (number == null) {
			if (other.number != null)
				return false;
		} else if (!number.equals(other.number))
			return false;
		if (pos == null) {
			if (other.pos != null)
				return false;
		} else if (!pos.equals(other.pos))
			return false;
		if (slotsAvailable == null) {
			if (other.slotsAvailable != null)
				return false;
		} else if (!slotsAvailable.equals(other.slotsAvailable))
			return false;
		if (state == null) {
			if (other.state != null)
				return false;
		} else if (!state.equals(other.state))
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
		return "Station [" + (number != null ? "number=" + number + ", " : "") + (name != null ? "name=" + name + ", " : "") + (address != null ? "address=" + address + ", " : "")
				+ (state != null ? "state=" + state + ", " : "") + (latitude != null ? "latitude=" + latitude + ", " : "") + (longitude != null ? "longitude=" + longitude + ", " : "")
				+ (slotsAvailable != null ? "slotsAvailable=" + slotsAvailable + ", " : "") + (bikesAvailable != null ? "bikesAvailable=" + bikesAvailable + ", " : "")
				+ (pos != null ? "pos=" + pos + ", " : "") + (district != null ? "district=" + district + ", " : "") + (lastUpdate != null ? "lastUpdate=" + lastUpdate : "") + "]";
	}
}