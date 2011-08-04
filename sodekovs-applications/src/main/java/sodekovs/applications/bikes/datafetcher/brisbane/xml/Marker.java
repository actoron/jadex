package sodekovs.applications.bikes.datafetcher.brisbane.xml;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * XML Representation for a Marker.
 *
 * @author Thomas Preisler
 */
public class Marker {
	
	private String name = null;
	
	private Integer number = 0;
	
	private String address = null;
	
	private String fullAddress = null;
	
	private Double lat = 0.0;
	
	private Double lng = 0.0;
	
	private Integer open = 0;
	
	private Integer bonus = 0;

	/**
	 * @return the name
	 */
	@XmlAttribute(name = "name")
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the number
	 */
	@XmlAttribute(name = "number")
	public Integer getNumber() {
		return number;
	}

	/**
	 * @param number the number to set
	 */
	public void setNumber(Integer number) {
		this.number = number;
	}

	/**
	 * @return the address
	 */
	@XmlAttribute(name = "address")
	public String getAddress() {
		return address;
	}

	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return the fullAddress
	 */
	@XmlAttribute(name = "fullAddress")
	public String getFullAddress() {
		return fullAddress;
	}

	/**
	 * @param fullAddress the fullAddress to set
	 */
	public void setFullAddress(String fullAddress) {
		this.fullAddress = fullAddress;
	}

	/**
	 * @return the lat
	 */
	@XmlAttribute(name = "lat")
	public Double getLat() {
		return lat;
	}

	/**
	 * @param lat the lat to set
	 */
	public void setLat(Double lat) {
		this.lat = lat;
	}

	/**
	 * @return the lng
	 */
	@XmlAttribute(name = "lng")
	public Double getLng() {
		return lng;
	}

	/**
	 * @param lng the lng to set
	 */
	public void setLng(Double lng) {
		this.lng = lng;
	}

	/**
	 * @return the open
	 */
	@XmlAttribute(name = "open")
	public Integer getOpen() {
		return open;
	}

	/**
	 * @param open the open to set
	 */
	public void setOpen(Integer open) {
		this.open = open;
	}

	/**
	 * @return the bonus
	 */
	@XmlAttribute(name = "bonus")
	public Integer getBonus() {
		return bonus;
	}

	/**
	 * @param bonus the bonus to set
	 */
	public void setBonus(Integer bonus) {
		this.bonus = bonus;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + ((bonus == null) ? 0 : bonus.hashCode());
		result = prime * result + ((fullAddress == null) ? 0 : fullAddress.hashCode());
		result = prime * result + ((lat == null) ? 0 : lat.hashCode());
		result = prime * result + ((lng == null) ? 0 : lng.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((number == null) ? 0 : number.hashCode());
		result = prime * result + ((open == null) ? 0 : open.hashCode());
		return result;
	}

	/* (non-Javadoc)
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
		Marker other = (Marker) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (bonus == null) {
			if (other.bonus != null)
				return false;
		} else if (!bonus.equals(other.bonus))
			return false;
		if (fullAddress == null) {
			if (other.fullAddress != null)
				return false;
		} else if (!fullAddress.equals(other.fullAddress))
			return false;
		if (lat == null) {
			if (other.lat != null)
				return false;
		} else if (!lat.equals(other.lat))
			return false;
		if (lng == null) {
			if (other.lng != null)
				return false;
		} else if (!lng.equals(other.lng))
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
		if (open == null) {
			if (other.open != null)
				return false;
		} else if (!open.equals(other.open))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Marker [" + (name != null ? "name=" + name + ", " : "") + (number != null ? "number=" + number + ", " : "") + (address != null ? "address=" + address + ", " : "")
				+ (fullAddress != null ? "fullAddress=" + fullAddress + ", " : "") + (lat != null ? "lat=" + lat + ", " : "") + (lng != null ? "lng=" + lng + ", " : "")
				+ (open != null ? "open=" + open + ", " : "") + (bonus != null ? "bonus=" + bonus : "") + "]";
	}
	
	
}
