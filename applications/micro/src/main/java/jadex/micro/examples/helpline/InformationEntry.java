package jadex.micro.examples.helpline;

import jadex.commons.SUtil;

/**
 *  Simple struct storing information about a person.
 */
public class InformationEntry implements Comparable
{
	//-------- attributes --------
	
	/** The person's name. */
	protected String name;
	
	/** The information. */
	protected String information;
	
	/** The date of the information. */
	protected long date;

	//-------- constructors --------
	
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

	//-------- methods --------
	
	/**
	 *  Get the name.
	 *  @return the name.
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
	 *  Get the hashcode.
	 */
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((information == null) ? 0 : information.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/**
	 *  Test if equal to another object.
	 */
	public boolean equals(Object obj)
	{
		boolean ret = false;
		if(obj instanceof InformationEntry)
		{
			InformationEntry other = (InformationEntry)obj;
			ret = SUtil.equals(name, other.name) && SUtil.equals(information, other.information);
				//&& date==other.date;
		}
		return ret;
	}

	/**
	 *  Compare this entry to another one.
	 */
	public int compareTo(Object o)
	{
		int ret = (int)(((InformationEntry)o).date-date);
		if(ret==0)
			ret = o.hashCode()-hashCode();
		return ret;
	}
}
