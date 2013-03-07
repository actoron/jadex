/**
 * 
 */
package sodekovs.bikesharing.data.clustering;

import java.util.ArrayList;
import java.util.List;

/**
 * @author thomas
 * 
 */
public class Quadrant {

	/*
	 * The borders
	 */
	private Double north = null;
	private Double south = null;
	private Double west = null;
	private Double east = null;

	private List<Station> stations = null;

	/**
	 * @param north
	 * @param south
	 * @param west
	 * @param east
	 */
	public Quadrant(Double north, Double south, Double west, Double east) {
		super();
		this.north = north;
		this.south = south;
		this.west = west;
		this.east = east;
		this.stations = new ArrayList<Station>();
	}

	/**
	 * @return the north
	 */
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
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final int maxLen = 10;
		return "Quadrant [north=" + north + ", south=" + south + ", west=" + west + ", east=" + east + ", stations="
				+ (stations != null ? stations.subList(0, Math.min(stations.size(), maxLen)) : null) + "]";
	}

	/**
	 * Add a station.
	 * 
	 * @param station
	 * @return
	 */
	public boolean addStation(Station station) {
		return this.stations.add(station);
	}

	public boolean isInQuadrant(Station station) {
		Double lat = station.getLat();
		Double lon = station.getLon();

		return (lat >= south && lat <= north && lon >= west && lon <= east);
	}

	public Integer getSize() {
		return stations.size();
	}

	public Quadrant[] createSubQuadrants() {
		Double nsMiddle = (north + south) / 2;
		Double weMiddle = (west + east) / 2;

		Quadrant nw = new Quadrant(north, nsMiddle, west, weMiddle);
		Quadrant ne = new Quadrant(north, nsMiddle, weMiddle, east);
		Quadrant sw = new Quadrant(nsMiddle, south, west, weMiddle);
		Quadrant se = new Quadrant(nsMiddle, south, weMiddle, east);

		for (Station station : stations) {
			if (nw.isInQuadrant(station)) {
				nw.addStation(station);
			} else if (ne.isInQuadrant(station)) {
				ne.addStation(station);
			} else if (sw.isInQuadrant(station)) {
				sw.addStation(station);
			} else if (se.isInQuadrant(station)) {
				se.addStation(station);
			} else {
				System.err.println("Station lost while creating sub quadrants " + station);
			}
		}

		Quadrant[] quadrants = new Quadrant[4];
		quadrants[0] = nw;
		quadrants[1] = ne;
		quadrants[2] = sw;
		quadrants[3] = se;

		return quadrants;
	}

	public List<Quadrant> createSubQuadrants(int level) {
		List<Quadrant> quadrants = new ArrayList<Quadrant>();

		if (level >= 1) {
			Double nsMiddle = (north + south) / 2;
			Double weMiddle = (west + east) / 2;

			Quadrant nw = new Quadrant(north, nsMiddle, west, weMiddle);
			Quadrant ne = new Quadrant(north, nsMiddle, weMiddle, east);
			Quadrant sw = new Quadrant(nsMiddle, south, west, weMiddle);
			Quadrant se = new Quadrant(nsMiddle, south, weMiddle, east);

			for (Station station : stations) {
				if (nw.isInQuadrant(station)) {
					nw.addStation(station);
				} else if (ne.isInQuadrant(station)) {
					ne.addStation(station);
				} else if (sw.isInQuadrant(station)) {
					sw.addStation(station);
				} else if (se.isInQuadrant(station)) {
					se.addStation(station);
				} else {
					System.err.println("Station lost while creating sub quadrants " + station);
				}
			}

			if (level == 1) {
				quadrants.add(nw);
				quadrants.add(ne);
				quadrants.add(sw);
				quadrants.add(se);
			}

			quadrants.addAll(nw.createSubQuadrants(level - 1));
			quadrants.addAll(ne.createSubQuadrants(level - 1));
			quadrants.addAll(sw.createSubQuadrants(level - 1));
			quadrants.addAll(se.createSubQuadrants(level - 1));
		}

		return quadrants;
	}
}