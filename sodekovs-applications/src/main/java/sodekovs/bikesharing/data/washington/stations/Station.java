package sodekovs.bikesharing.data.washington.stations;

import javax.xml.bind.annotation.XmlElement;

/**
 * This class represents a bike station with all the according data.
 * 
 * @author Thomas Preisler
 */
public class Station {

	/** The stations id */
	private Integer id = -1;

	/** The stations name */
	private String name = null;

	/** The terminal name */
	private String terminalName = null;

	/** The stations latitude */
	private Double lat = 0.0;

	/** The stations longitude */
	private Double lon = 0.0;

	/** Is the station available for usage? */
	private Boolean installed = false;

	/** Is the station locked? */
	private Boolean locked = false;

	/** Unix timestamp for the installation date of the station */
	private Long installDate = -1L;

	/** Unix timestamp for the removal fate of the station */
	private Long removalDate = -1L;

	/** Is this just a temporary station? */
	private Boolean temporary = false;

	/** The number of bikes at the station */
	private Integer nbBikes = -1;

	/** The number of empty docks at the station */
	private Integer nbEmptyDocks = -1;

	/** The number of docks at the station */
	private Integer nbDocks = -1;

	/**
	 * @return the id
	 */
	@XmlElement(name = "id")
	public Integer getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
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
	 * @return the terminalName
	 */
	@XmlElement(name = "terminalName")
	public String getTerminalName() {
		return terminalName;
	}

	/**
	 * @param terminalName
	 *            the terminalName to set
	 */
	public void setTerminalName(String terminalName) {
		this.terminalName = terminalName;
	}

	/**
	 * @return the lat
	 */
	@XmlElement(name = "lat")
	public Double getLat() {
		return lat;
	}

	/**
	 * @param lat
	 *            the lat to set
	 */
	public void setLat(Double lat) {
		this.lat = lat;
	}

	/**
	 * @return the lon
	 */
	@XmlElement(name = "long")
	public Double getLon() {
		return lon;
	}

	/**
	 * @param lon
	 *            the lon to set
	 */
	public void setLon(Double lon) {
		this.lon = lon;
	}

	/**
	 * @return the installed
	 */
	@XmlElement(name = "installed")
	public Boolean getInstalled() {
		return installed;
	}

	/**
	 * @param installed
	 *            the installed to set
	 */
	public void setInstalled(Boolean installed) {
		this.installed = installed;
	}

	/**
	 * @return the locked
	 */
	@XmlElement(name = "locked")
	public Boolean getLocked() {
		return locked;
	}

	/**
	 * @param locked
	 *            the locked to set
	 */
	public void setLocked(Boolean locked) {
		this.locked = locked;
	}

	/**
	 * @return the installDate
	 */
	@XmlElement(name = "installDate")
	public Long getInstallDate() {
		return installDate;
	}

	/**
	 * @param installDate
	 *            the installDate to set
	 */
	public void setInstallDate(Long installDate) {
		this.installDate = installDate;
	}

	/**
	 * @return the removalDate
	 */
	@XmlElement(name = "removalDate")
	public Long getRemovalDate() {
		return removalDate;
	}

	/**
	 * @param removalDate
	 *            the removalDate to set
	 */
	public void setRemovalDate(Long removalDate) {
		this.removalDate = removalDate;
	}

	/**
	 * @return the temporary
	 */
	@XmlElement(name = "temporary")
	public Boolean getTemporary() {
		return temporary;
	}

	/**
	 * @param temporary
	 *            the temporary to set
	 */
	public void setTemporary(Boolean temporary) {
		this.temporary = temporary;
	}

	/**
	 * @return the nbBikes
	 */
	@XmlElement(name = "nbBikes")
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
	@XmlElement(name = "nbEmptyDocks")
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
	 * @return the nbDocks
	 */
	@XmlElement(name = "nbDocks")
	public Integer getNbDocks() {
		return nbDocks;
	}

	/**
	 * @param nbDocks
	 *            the nbDocks to set
	 */
	public void setNbDocks(Integer nbDocks) {
		this.nbDocks = nbDocks;
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
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((installDate == null) ? 0 : installDate.hashCode());
		result = prime * result + ((installed == null) ? 0 : installed.hashCode());
		result = prime * result + ((lat == null) ? 0 : lat.hashCode());
		result = prime * result + ((locked == null) ? 0 : locked.hashCode());
		result = prime * result + ((lon == null) ? 0 : lon.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((nbBikes == null) ? 0 : nbBikes.hashCode());
		result = prime * result + ((nbDocks == null) ? 0 : nbDocks.hashCode());
		result = prime * result + ((nbEmptyDocks == null) ? 0 : nbEmptyDocks.hashCode());
		result = prime * result + ((removalDate == null) ? 0 : removalDate.hashCode());
		result = prime * result + ((temporary == null) ? 0 : temporary.hashCode());
		result = prime * result + ((terminalName == null) ? 0 : terminalName.hashCode());
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
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (installDate == null) {
			if (other.installDate != null)
				return false;
		} else if (!installDate.equals(other.installDate))
			return false;
		if (installed == null) {
			if (other.installed != null)
				return false;
		} else if (!installed.equals(other.installed))
			return false;
		if (lat == null) {
			if (other.lat != null)
				return false;
		} else if (!lat.equals(other.lat))
			return false;
		if (locked == null) {
			if (other.locked != null)
				return false;
		} else if (!locked.equals(other.locked))
			return false;
		if (lon == null) {
			if (other.lon != null)
				return false;
		} else if (!lon.equals(other.lon))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (nbBikes == null) {
			if (other.nbBikes != null)
				return false;
		} else if (!nbBikes.equals(other.nbBikes))
			return false;
		if (nbDocks == null) {
			if (other.nbDocks != null)
				return false;
		} else if (!nbDocks.equals(other.nbDocks))
			return false;
		if (nbEmptyDocks == null) {
			if (other.nbEmptyDocks != null)
				return false;
		} else if (!nbEmptyDocks.equals(other.nbEmptyDocks))
			return false;
		if (removalDate == null) {
			if (other.removalDate != null)
				return false;
		} else if (!removalDate.equals(other.removalDate))
			return false;
		if (temporary == null) {
			if (other.temporary != null)
				return false;
		} else if (!temporary.equals(other.temporary))
			return false;
		if (terminalName == null) {
			if (other.terminalName != null)
				return false;
		} else if (!terminalName.equals(other.terminalName))
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
		return "Station [" + (id != null ? "id=" + id + ", " : "") + (name != null ? "name=" + name + ", " : "") + (terminalName != null ? "terminalName=" + terminalName + ", " : "")
				+ (lat != null ? "lat=" + lat + ", " : "") + (lon != null ? "lon=" + lon + ", " : "") + (installed != null ? "installed=" + installed + ", " : "")
				+ (locked != null ? "locked=" + locked + ", " : "") + (installDate != null ? "installDate=" + installDate + ", " : "")
				+ (removalDate != null ? "removalDate=" + removalDate + ", " : "") + (temporary != null ? "temporary=" + temporary + ", " : "") + (nbBikes != null ? "nbBikes=" + nbBikes + ", " : "")
				+ (nbEmptyDocks != null ? "nbEmptyBikes=" + nbEmptyDocks + ", " : "") + (nbDocks != null ? "nbDocks=" + nbDocks : "") + "]";
	}
}
