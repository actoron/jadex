package sodekovs.marsworld.coordination;

import java.io.Serializable;

/**
 * Serializable class to send data from the application space related to the coordination.
 * 
 * @author Thomas Preisler
 */
public class CoordinationSpaceData implements Serializable {
	
	private static final long serialVersionUID = -696342769239394684L;
	
	private Double x = null, y = null;
	
	/**
	 * Default constructor
	 */
	public CoordinationSpaceData() {
		super();
	}

	/**
	 * @return the x
	 */
	public Double getX() {
		return x;
	}

	/**
	 * @param x the x to set
	 */
	public void setX(Double x) {
		this.x = x;
	}

	/**
	 * @return the y
	 */
	public Double getY() {
		return y;
	}

	/**
	 * @param y the y to set
	 */
	public void setY(Double y) {
		this.y = y;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CoordinationSpaceData [" + (x != null ? "x=" + x + ", " : "") + (y != null ? "y=" + y : "") + "]";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((x == null) ? 0 : x.hashCode());
		result = prime * result + ((y == null) ? 0 : y.hashCode());
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
		CoordinationSpaceData other = (CoordinationSpaceData) obj;
		if (x == null) {
			if (other.x != null)
				return false;
		} else if (!x.equals(other.x))
			return false;
		if (y == null) {
			if (other.y != null)
				return false;
		} else if (!y.equals(other.y))
			return false;
		return true;
	}

	/**
	 * @param x
	 * @param y
	 */
	public CoordinationSpaceData(Double x, Double y) {
		super();
		this.x = x;
		this.y = y;
	}
}