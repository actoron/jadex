package jadex.micro.examples.ws.banking;

import java.util.Date;

/**
 * 
 */
public class Request
{
	protected Date begin;
	
	protected Date end;

	/**
	 * 
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
