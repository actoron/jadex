package sodekovs.applications.bikes.datafetcher.brisbane.xml;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * XML Representation for a Station.
 * 
 * @author Thomas Preisler
 */
@XmlRootElement(name = "station")
public class Station {
	
	private Integer available = 0;
	
	private Integer free = 0;
	
	private Integer total = 0;
	
	private Integer ticket = 0;

	/**
	 * @return the available
	 */
	public Integer getAvailable() {
		return available;
	}

	/**
	 * @param available the available to set
	 */
	public void setAvailable(Integer available) {
		this.available = available;
	}

	/**
	 * @return the free
	 */
	public Integer getFree() {
		return free;
	}

	/**
	 * @param free the free to set
	 */
	public void setFree(Integer free) {
		this.free = free;
	}

	/**
	 * @return the total
	 */
	public Integer getTotal() {
		return total;
	}

	/**
	 * @param total the total to set
	 */
	public void setTotal(Integer total) {
		this.total = total;
	}

	/**
	 * @return the ticket
	 */
	public Integer getTicket() {
		return ticket;
	}

	/**
	 * @param ticket the ticket to set
	 */
	public void setTicket(Integer ticket) {
		this.ticket = ticket;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((available == null) ? 0 : available.hashCode());
		result = prime * result + ((free == null) ? 0 : free.hashCode());
		result = prime * result + ((ticket == null) ? 0 : ticket.hashCode());
		result = prime * result + ((total == null) ? 0 : total.hashCode());
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
		Station other = (Station) obj;
		if (available == null) {
			if (other.available != null)
				return false;
		} else if (!available.equals(other.available))
			return false;
		if (free == null) {
			if (other.free != null)
				return false;
		} else if (!free.equals(other.free))
			return false;
		if (ticket == null) {
			if (other.ticket != null)
				return false;
		} else if (!ticket.equals(other.ticket))
			return false;
		if (total == null) {
			if (other.total != null)
				return false;
		} else if (!total.equals(other.total))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Station [" + (available != null ? "available=" + available + ", " : "") + (free != null ? "free=" + free + ", " : "") + (total != null ? "total=" + total + ", " : "")
				+ (ticket != null ? "ticket=" + ticket : "") + "]";
	}
}