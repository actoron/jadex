package sodekovs.bikesharing.coordination;

import jadex.extension.envsupport.math.Vector2Double;

/**
 * OccupancyTupel containing the id, the stock and the capacity of a bike station. Passed as coordination information from a station to the according coordination mechanism.
 * 
 * @author Thomas Preisler
 */
public class OccupancyTuple {

	private String stationId = null;
	private Integer stock = null;
	private Integer capacity = null;
	private Vector2Double position = null;
	
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
	 * @return the stationId
	 */
	public String getStationId() {
		return stationId;
	}
	/**
	 * @param stationId the stationId to set
	 */
	public void setStationId(String stationId) {
		this.stationId = stationId;
	}
	/**
	 * @return the stock
	 */
	public Integer getStock() {
		return stock;
	}
	/**
	 * @param stock the stock to set
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
		result = prime * result + ((stationId == null) ? 0 : stationId.hashCode());
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
		OccupancyTuple other = (OccupancyTuple) obj;
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
		if (stationId == null) {
			if (other.stationId != null)
				return false;
		} else if (!stationId.equals(other.stationId))
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
		return "OccupancyTuple [stationId=" + stationId + ", stock=" + stock + ", capacity=" + capacity + ", position=" + position + "]";
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