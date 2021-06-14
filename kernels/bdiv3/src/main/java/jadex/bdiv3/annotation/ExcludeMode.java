package jadex.bdiv3.annotation;

/** The exclude mode determines when and if a plan is removed from the applicable plans list (APL). */
public enum ExcludeMode
{
	/** The plan is never removed. */
	Never("never"),

	/** The plan is removed after it has been executed once, regardless of success or failure or abortion. */
	WhenTried("when_tried"),
	
	/** The plan is removed after it has been executed once, but only when it exited with an exception. */
	WhenFailed("when_failed"),
	
	/** The plan is removed after it has been executed once, but only when it exited without an exception. */
	WhenSucceeded("when_succeeded");
	
	protected String str;
	
	/**
	 *  Create a new direction
	 */
	ExcludeMode(String str)
	{
		this.str = str;
	} 
	
	/**
	 *  Convert from string to enum.
	 */
	public static ExcludeMode getExcludeMode(String name)
	{
		for(ExcludeMode em: ExcludeMode.values())
		{
			if(em.str.equals(name))
				return em;
		}
		throw new IllegalArgumentException("No such exlude mode: "+name);
	}
}