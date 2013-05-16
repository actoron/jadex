package jadex.commons;

/**
 *  A three valued boolean to be used in annotations.
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
}
