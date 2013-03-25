/**
 * 
 */
package sodekovs.bikesharing.coordination;

import jadex.extension.envsupport.math.Vector2Double;

/**
 * @author thomas
 *
 */
public class CoordinationStationData {
	
	private String stationID = null;
	
	private Integer stock = null;
	
	private Integer capacity = null;
	
	private Vector2Double position = null;

	public CoordinationStationData() {
		super();
	}
	
	public CoordinationStationData(String stationId, Integer capacity, Integer stock, Vector2Double position) {
		this.stationID = stationId;
		this.capacity = capacity;
		this.stock = stock;
		this.position = position;
	}

	/**
	 * @return the position
	 */
	public Vector2Double getPosition() {
		return position;
	}

	/**
	 * @param position the position to set
	 */
	public void setPosition(Vector2Double position) {
		this.position = position;
	}

	/**
	 * @return the stationID
	 */
	public String getStationID() {
		return stationID;
	}

	/**
	 * @param stationID the stationID to set
	 */
	public void setStationID(String stationID) {
		this.stationID = stationID;
	}

	/**
	 * @return the stocks
	 */
	public Integer getStock() {
		return stock;
	}

	/**
	 * @param stock the stocks to set
	 */
	public void setStock(Integer stock) {
		this.stock = stock;
	}

	/**
	 * @return the capacity
	 */
	public Integer getCapacity() {
		return capacity;
	}

	/**
	 * @param capacity the capacity to set
	 */
	public void setCapacity(Integer capacity) {
		this.capacity = capacity;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((capacity == null) ? 0 : capacity.hashCode());
		result = prime * result + ((position == null) ? 0 : position.hashCode());
		result = prime * result + ((stationID == null) ? 0 : stationID.hashCode());
		result = prime * result + ((stock == null) ? 0 : stock.hashCode());
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
		CoordinationStationData other = (CoordinationStationData) obj;
		if (capacity == null) {
			if (other.capacity != null)
				return false;
		} else if (!capacity.equals(other.capacity))
			return false;
		if (position == null) {
			if (other.position != null)
				return false;
		} else if (!position.equals(other.position))
			return false;
		if (stationID == null) {
			if (other.stationID != null)
				return false;
		} else if (!stationID.equals(other.stationID))
			return false;
		if (stock == null) {
			if (other.stock != null)
				return false;
		} else if (!stock.equals(other.stock))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CoordinationStationData [stationID=" + stationID + ", stock=" + stock + ", capacity=" + capacity + ", position=" + position + "]";
	}
	
	/**
	 * Returns the occupancy for given capacity and stock values or <code>null</code> if one of the two given values if <code>null</code>
	 * @return
	 */
	public Double getOccupancy() {
		if (capacity != null && stock != null) {
			return Double.valueOf(stock) / Double.valueOf(capacity);
		}
		
		return null;
	}
}