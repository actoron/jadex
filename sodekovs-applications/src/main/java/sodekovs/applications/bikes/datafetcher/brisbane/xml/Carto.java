package sodekovs.applications.bikes.datafetcher.brisbane.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * XML Representation for a Carto.
 * 
 * @author Thomas Preisler
 */
@XmlRootElement(name = "carto")
public class Carto {
	
	private List<Marker> markers = new ArrayList<Marker>();
	
	private List<Arrondissement> arrondissements = new ArrayList<Arrondissement>();

	/**
	 * @return the markers
	 */
	@XmlElementWrapper(name = "markers")
	@XmlElement(name = "marker")
	public List<Marker> getMarkers() {
		return markers;
	}

	/**
	 * @param markers the markers to set
	 */
	public void setMarkers(List<Marker> markers) {
		this.markers = markers;
	}

	/**
	 * @return the arrondissements
	 */
	@XmlElementWrapper(name = "arrondissements")
	@XmlElement(name = "arrondissement")
	public List<Arrondissement> getArrondissements() {
		return arrondissements;
	}

	/**
	 * @param arrondissements the arrondissements to set
	 */
	public void setArrondissements(List<Arrondissement> arrondissements) {
		this.arrondissements = arrondissements;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((arrondissements == null) ? 0 : arrondissements.hashCode());
		result = prime * result + ((markers == null) ? 0 : markers.hashCode());
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
		Carto other = (Carto) obj;
		if (arrondissements == null) {
			if (other.arrondissements != null)
				return false;
		} else if (!arrondissements.equals(other.arrondissements))
			return false;
		if (markers == null) {
			if (other.markers != null)
				return false;
		} else if (!markers.equals(other.markers))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final int maxLen = 10;
		return "Carto [" + (markers != null ? "markers=" + markers.subList(0, Math.min(markers.size(), maxLen)) + ", " : "")
				+ (arrondissements != null ? "arrondissements=" + arrondissements.subList(0, Math.min(arrondissements.size(), maxLen)) : "") + "]";
	}
}
