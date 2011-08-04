package sodekovs.applications.bikes.datafetcher.brisbane.xml;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * XML Representation for an Arrondissement.
 * 
 * @author Thomas Preisler
 */
public class Arrondissement {
	
	private Integer number = -1;
	
	private Double minLat = 0.0;
	
	private Double minLng = 0.0;
	
	private Double maxLat = 0.0;
	
	private Double maxLng = 0.0;

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
	 * @return the minLat
	 */
	@XmlAttribute(name = "minLat")
	public Double getMinLat() {
		return minLat;
	}

	/**
	 * @param minLat the minLat to set
	 */
	public void setMinLat(Double minLat) {
		this.minLat = minLat;
	}

	/**
	 * @return the minLng
	 */
	@XmlAttribute(name = "minLng")
	public Double getMinLng() {
		return minLng;
	}

	/**
	 * @param minLng the minLng to set
	 */
	public void setMinLng(Double minLng) {
		this.minLng = minLng;
	}

	/**
	 * @return the maxLat
	 */
	@XmlAttribute(name = "maxLat")
	public Double getMaxLat() {
		return maxLat;
	}

	/**
	 * @param maxLat the maxLat to set
	 */
	public void setMaxLat(Double maxLat) {
		this.maxLat = maxLat;
	}

	/**
	 * @return the maxLng
	 */
	@XmlAttribute(name = "maxLng")
	public Double getMaxLng() {
		return maxLng;
	}

	/**
	 * @param maxLng the maxLng to set
	 */
	public void setMaxLng(Double maxLng) {
		this.maxLng = maxLng;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((maxLat == null) ? 0 : maxLat.hashCode());
		result = prime * result + ((maxLng == null) ? 0 : maxLng.hashCode());
		result = prime * result + ((minLat == null) ? 0 : minLat.hashCode());
		result = prime * result + ((minLng == null) ? 0 : minLng.hashCode());
		result = prime * result + ((number == null) ? 0 : number.hashCode());
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
		Arrondissement other = (Arrondissement) obj;
		if (maxLat == null) {
			if (other.maxLat != null)
				return false;
		} else if (!maxLat.equals(other.maxLat))
			return false;
		if (maxLng == null) {
			if (other.maxLng != null)
				return false;
		} else if (!maxLng.equals(other.maxLng))
			return false;
		if (minLat == null) {
			if (other.minLat != null)
				return false;
		} else if (!minLat.equals(other.minLat))
			return false;
		if (minLng == null) {
			if (other.minLng != null)
				return false;
		} else if (!minLng.equals(other.minLng))
			return false;
		if (number == null) {
			if (other.number != null)
				return false;
		} else if (!number.equals(other.number))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Arrondissement [" + (number != null ? "number=" + number + ", " : "") + (minLat != null ? "minLat=" + minLat + ", " : "") + (minLng != null ? "minLng=" + minLng + ", " : "")
				+ (maxLat != null ? "maxLat=" + maxLat + ", " : "") + (maxLng != null ? "maxLng=" + maxLng : "") + "]";
	}
}