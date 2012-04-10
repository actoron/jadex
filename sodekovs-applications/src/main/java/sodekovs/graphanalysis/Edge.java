/**
 * 
 */
package sodekovs.graphanalysis;

import java.io.Serializable;

/**
 * @author thomas
 * 
 */
public class Edge implements Comparable<Edge>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2416324120474719090L;

	private int weight = 1;
	private int id = 0;

	/**
	 * @param id
	 */
	public Edge(int id) {
		super();
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.valueOf(weight);
	}

	/**
	 * @return the weight
	 */
	public int getWeight() {
		return weight;
	}

	/**
	 * @param weight
	 *            the weight to set
	 */
	public void setWeight(int weight) {
		this.weight = weight;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	public void incrementWeight() {
		this.weight++;
	}

	@Override
	public int compareTo(Edge o) {
		if (this.weight > o.weight)
			return 1;
		else if (this.weight < o.weight)
			return -1;
		else
			return new Integer(id).compareTo(o.id);
	}
}
