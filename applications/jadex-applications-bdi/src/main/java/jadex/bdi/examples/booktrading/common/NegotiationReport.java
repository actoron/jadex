package jadex.bdi.examples.booktrading.common;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *  A negotiation report contains user-relevant data about negotiations,
 *  i.e. the order and details about the negotiation and the time.
 */
public class NegotiationReport
{
	//-------- attributes --------
	
	/** The order. */
	protected Order order;
	
	/** The report. */
	protected String details;
	
	/** The negotiation time. */
	protected long time;
	
	
	//-------- constructors --------
	
	/**
	 *  Create a new report.
	 */
	public NegotiationReport(Order order, String details, long time)
	{
		this.order = order;
		this.details = details;
		this.time = time;
	}

	//-------- methods --------
	
	/**
	 *  Get the order.
	 *  @return The order.
	 */
	public Order getOrder()
	{
		return order;
	}

	/**
	 *  Set the order.
	 *  @param order The order to set.
	 */
	public void setOrder(Order order)
	{
		this.order = order;
	}

	/**
	 *  Get the details.
	 *  @return The details.
	 */
	public String getDetails()
	{
		return details;
	}

	/**
	 *  Set the details.
	 *  @param details The details to set.
	 */
	public void setDetails(String details)
	{
		this.details = details;
	}
	
	/**
	 *  Get the negotiation time.
	 *  @return The time.
	 */
	public long getTime()
	{
		return time;
	}

	/**
	 *  Set the negotiation time.
	 *  @param time The time to set.
	 */
	public void setTime(long time)
	{
		this.time = time;
	}

	/** 
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		//return "NegotiationReport("+order+", "+details+")";
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy.'-'HH:mm:ss ': '");
		return sdf.format(new Date(time))+order+" - "+details;
	}
}
