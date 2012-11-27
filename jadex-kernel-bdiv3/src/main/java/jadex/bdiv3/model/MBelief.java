package jadex.bdiv3.model;


/**
 *  Belief model.
 */
public class MBelief extends MElement
{
	/** The target. */
	protected FieldInfo target;

	/** The collection implementation class. */
	protected String impl;
	
	/**
	 *  Create a new belief.
	 */
	public MBelief(FieldInfo target, String impl)
	{
		super(target.getName());
		this.target = target;
		this.impl = impl;
	}

	/**
	 *  Get the target.
	 *  @return The target.
	 */
	public FieldInfo getTarget()
	{
		return target;
	}

	/**
	 *  Set the target.
	 *  @param target The target to set.
	 */
	public void setTarget(FieldInfo target)
	{
		this.target = target;
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
