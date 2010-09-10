package jadex.micro.examples.helpline;

/**
 * 
 */
public class InformationEntry implements Comparable
{
	/** The person name. */
	protected String name;
	
	/** The information. */
	protected String information;
	
	/** The date of the information. */
	protected long date;

	/**
	 * 
	 */
	public InformationEntry()
	{
	}

	/**
	 * 	
	 */
	public InformationEntry(String name, String information, long date)
	{
		this.name = name;
		this.information = information;
		this.date = date;
	}

	/**
	 *  Get the name.
	 *  @return The name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 *  Set the name.
	 *  @param name The name to set.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 *  Get the information.
	 *  @return The information.
	 */
	public String getInformation()
	{
		return information;
	}

	/**
	 *  Set the information.
	 *  @param information The information to set.
	 */
	public void setInformation(String information)
	{
		this.information = information;
	}

	/**
	 *  Get the date.
	 *  @return The date.
	 */
	public long getDate()
	{
		return date;
	}

	/**
	 *  Set the date.
	 *  @param date The date to set.
	 */
	public void setDate(long date)
	{
		this.date = date;
	}
	
	/**
	 * 
	 */
	public int compareTo(Object o)
	{
		int ret = (int)(((InformationEntry)o).date-date);
		if(ret==0)
			ret = o.hashCode()-hashCode();
		return ret;
	}
}
