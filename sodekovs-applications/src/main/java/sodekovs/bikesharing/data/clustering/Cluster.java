package sodekovs.bikesharing.data.clustering;

import java.util.List;
import java.util.Random;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Cluster of bike stations.
 * 
 * @author Thomas Preisler
 */
@XmlRootElement(name = "Cluster")
public class Cluster {

	/*
	 * The borders
	 */
	private Double north = null;
	private Double south = null;
	private Double west = null;
	private Double east = null;

	/**
	 * List of stations
	 */
	private List<Station> stations = null;

	/**
	 * The super station
	 */
	private Station superStation = null;

	/**
	 * Default constructor
	 */
	public Cluster() {
		super();
	}

	/**
	 * Creates a cluster from the given {@link Quadrant} and selects randomly one of the stations as super station.
	 * 
	 * @param quadrant
	 */
	public Cluster(Quadrant quadrant) {
		this.north = quadrant.getNorth();
		this.south = quadrant.getSouth();
		this.west = quadrant.getWest();
		this.east = quadrant.getEast();
		this.stations = quadrant.getStations();

		if (!this.stations.isEmpty()) {
			// assign a random super station
			Random rnd = new Random();
			this.superStation = this.stations.get(rnd.nextInt(this.stations.size()));
		}
	}

	/**
	 * @return the north
	 */
	@XmlAttribute(name = "north", required = true)
	public Double getNorth() {
		return north;
	}

	/**
	 * @param north
	 *            the north to set
	 */
	public void setNorth(Double north) {
		this.north = north;
	}

	/**
	 * @return the south
	 */
	@XmlAttribute(name = "south", required = true)
	public Double getSouth() {
		return south;
	}

	/**
	 * @param south
	 *            the south to set
	 */
	public void setSouth(Double south) {
		this.south = south;
	}

	/**
	 * @return the west
	 */
	@XmlAttribute(name = "west", required = true)
	public Double getWest() {
		return west;
	}

	/**
	 * @param west
	 *            the west to set
	 */
	public void setWest(Double west) {
		this.west = west;
	}

	/**
	 * @return the east
	 */
	@XmlAttribute(name = "east", required = true)
	public Double getEast() {
		return east;
	}

	/**
	 * @param east
	 *            the east to set
	 */
	public void setEast(Double east) {
		this.east = east;
	}

	/**
	 * @return the stations
	 */
	@XmlElementWrapper(name = "stations")
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

	/**
	 * @return the superStation
	 */
	@XmlElement(name = "superStation", required = true)
	public Station getSuperStation() {
		return superStation;
	}

	/**
	 * @param superStation
	 *            the superStation to set
	 */
	public void setSuperStation(Station superStation) {
		this.superStation = superStation;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final int maxLen = 10;
		return "Cluster [north=" + north + ", south=" + south + ", west=" + west + ", east=" + east + ", stations="
				+ (stations != null ? stations.subList(0, Math.min(stations.size(), maxLen)) : null) + ", superStation=" + superStation + "]";
	}
}