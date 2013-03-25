/**
 * 
 */
package sodekovs.bikesharing.coordination;

import jadex.extension.envsupport.math.Vector2Double;

/**
 * Extends the {@link CoordinationStationData} by adding a state.
 * 
 * @author Thomas Preisler
 */
public class StateCoordinationStationData extends CoordinationStationData {

	public static final Integer REQUEST = 0;
	public static final Integer REPLY = 1;
	
	private Integer state =  null;
	
	private String originatorID = null;

	public StateCoordinationStationData(String stationID, Integer capacity, Integer stock, Vector2Double position, Integer state, String originatorID) {
		super(stationID, capacity, stock, position);
		this.state = state;
		this.originatorID = originatorID;
	}

	/**
	 * @return the originatorID
	 */
	public String getOriginatorID() {
		return originatorID;
	}

	/**
	 * @param originatorID the originatorID to set
	 */
	public void setOriginatorID(String originatorID) {
		this.originatorID = originatorID;
	}

	/**
	 * @return the state
	 */
	public Integer getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(Integer state) {
		this.state = state;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((originatorID == null) ? 0 : originatorID.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		StateCoordinationStationData other = (StateCoordinationStationData) obj;
		if (originatorID == null) {
			if (other.originatorID != null)
				return false;
		} else if (!originatorID.equals(other.originatorID))
			return false;
		if (state == null) {
			if (other.state != null)
				return false;
		} else if (!state.equals(other.state))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "StateCoordinationStationData [state=" + state + ", originatorID=" + originatorID + "]";
	}
}