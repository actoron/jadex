package jadex.bridge.service.types.security;

/**
 * 
 */
public class KeyStoreEntry
{
	/** The entry type. */
	protected String type;
	
	/** The alias. */
	protected String alias;
	
	/** The date. */
	protected long date;
	
	/** The details. */
	protected Object details;

	/**
	 *  Create a new KeyStroreEntry. 
	 */
	public KeyStoreEntry()
	{
	}
	
	/**
	 *  Create a new KeyStroreEntry. 
	 */
	public KeyStoreEntry(String type, String alias, long date, Object details)
	{
		this.type = type;
		this.alias = alias;
		this.date = date;
		this.details = details;
	}

	/**
	 *  Get the type.
	 *  @return The type.
	 */
	public String getType()
	{
		return type;
	}

	/**
	 *  Set the type.
	 *  @param type The type to set.
	 */
	public void setType(String type)
	{
		this.type = type;
	}

	/**
	 *  Get the alias.
	 *  @return The alias.
	 */
	public String getAlias()
	{
		return alias;
	}

	/**
	 *  Set the alias.
	 *  @param alias The alias to set.
	 */
	public void setAlias(String alias)
	{
		this.alias = alias;
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
	 *  Get the details.
	 *  @return The details.
	 */
	public Object getDetails()
	{
		return details;
	}

	/**
	 *  Set the details.
	 *  @param details The details to set.
	 */
	public void setDetails(Object details)
	{
		this.details = details;
	}
}