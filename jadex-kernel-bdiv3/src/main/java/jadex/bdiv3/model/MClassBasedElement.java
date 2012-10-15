package jadex.bdiv3.model;

/**
 * 
 */
public class MClassBasedElement extends MProcessableElement
{
	/** The target. */
	protected Class<?> target;
	
	/**
	 *  Create a new belief.
	 */
	public MClassBasedElement(Class<?> target, boolean posttoall, boolean randomselection, String excludemode)
	{
		super(target.getName(), posttoall, randomselection, excludemode);
		this.target = target;
	}

	/**
	 *  Get the target.
	 *  @return The target.
	 */
	public Class<?> getTarget()
	{
		return target;
	}

	/**
	 *  Set the target.
	 *  @param target The target to set.
	 */
	public void setTarget(Class<?> target)
	{
		this.target = target;
	}
	
	/**
	 * 
	 */
	public boolean equals(Object other)
	{
		return other instanceof MGoal && target.equals(((MGoal)other).getTarget());
	}

	/**
	 * 
	 */
	public int hashCode()
	{
		return target.hashCode()*23;
	}
}
