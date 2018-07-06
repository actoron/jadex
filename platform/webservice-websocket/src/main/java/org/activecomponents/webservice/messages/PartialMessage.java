package org.activecomponents.webservice.messages;

/**
 *  Helper message to transport larger messages in slices.
 */
public class PartialMessage extends BaseMessage
{
	/** The data. */
	protected String data;
	
	/** The part number. */
	protected int number;
	
	/** The max number. */
	protected int count;

	/**
	 *  Create a new PartialMessage.
	 */
	public PartialMessage()
	{
	}
	
	/**
	 *  Create a new PartialMessage.
	 */
	public PartialMessage(String data, int number, int count)
	{
		this.data = data;
		this.number = number;
		this.count = count;
	}

	/**
	 *  Get the data.
	 *  @return the data
	 */
	public String getData()
	{
		return data;
	}

	/**
	 *  Set the data.
	 *  @param data The data to set
	 */
	public void setData(String data)
	{
		this.data = data;
	}

	/**
	 *  Get the number.
	 *  @return the number
	 */
	public int getNumber()
	{
		return number;
	}

	/**
	 *  Set the number.
	 *  @param number The number to set
	 */
	public void setNumber(int number)
	{
		this.number = number;
	}

	/**
	 *  Get the count.
	 *  @return the count
	 */
	public int getCount()
	{
		return count;
	}

	/**
	 *  Set the count.
	 *  @param count The count to set
	 */
	public void setCount(int count)
	{
		this.count = count;
	}
}
