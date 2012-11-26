package jadex.bdiv3.model;

/**
 * 
 */
public class MBeliefCollection extends MBelief
{
	/** The collection implementation class. */
	protected String impl;
	
	/**
	 *  Create a new belief.
	 */
	public MBeliefCollection(FieldInfo target, String impl)
	{
		super(target);
		this.impl = impl;
	}

	/**
	 *  Get the impl.
	 *  @return The impl.
	 */
	public String getImplClassName()
	{
		return impl;
	}

	/**
	 *  Set the impl.
	 *  @param impl The impl to set.
	 */
	public void setImplClassName(String impl)
	{
		this.impl = impl;
	}
}
