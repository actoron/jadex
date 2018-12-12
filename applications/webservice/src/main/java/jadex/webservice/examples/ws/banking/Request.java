package jadex.webservice.examples.ws.banking;

import java.util.Date;

/**
 *  Request that contains start an end date.
 */
public class Request
{
	//-------- attributes --------
	
	/** The start date. */
	protected Date begin;
	
	/** The end date. */
	protected Date end;

	//-------- constructors --------
	
	/**
	 *  Create a request.
	 */
	public Request()
	{
	}

	/**
	 * @param begin
	 * @param end
	 */
	public Request(Date begin, Date end)
	{
		this.begin = begin;
		this.end = end;
	}

	//-------- methods --------
	
	/**
	 *  Get the begin.
	 *  @return the begin.
	 */
	public Date getBegin()
	{
		return begin;
	}

	/**
	 *  Set the begin.
	 *  @param begin The begin to set.
	 */
	public void setBegin(Date begin)
	{
		this.begin = begin;
	}

	/**
	 *  Get the end.
	 *  @return the end.
	 */
	public Date getEnd()
	{
		return end;
	}

	/**
	 *  Set the end.
	 *  @param end The end to set.
	 */
	public void setEnd(Date end)
	{
		this.end = end;
	}

}
