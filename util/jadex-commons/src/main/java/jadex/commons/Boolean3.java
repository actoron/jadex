package jadex.commons;

/**
 *  A three valued boolean to be used in annotations as they do not allow null values.
 */
public enum Boolean3
{
	/** The undefined value. */
	NULL,
	
	/** The false value. */
	FALSE,
	
	/** The true value. */
	TRUE;

	/**
	 *  Convert the three valued boolean to a two valued boolean object.
	 */
	public Boolean	toBoolean()
	{
		return this==NULL ? null
			: this==FALSE ? Boolean.FALSE
			: Boolean.TRUE;
	}
	
	/**
	 *  Check if explicitly set to true, i.e. neither FALSE nor NULL.
	 */
	public boolean isTrue()
	{
		return this==TRUE;
	}
}
