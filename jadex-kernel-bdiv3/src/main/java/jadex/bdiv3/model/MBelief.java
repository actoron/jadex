package jadex.bdiv3.model;


/**
 *  Belief model.
 */
public class MBelief extends MElement
{
	/** The target. */
	protected FieldInfo target;
	
	/**
	 *  Create a new belief.
	 */
	public MBelief(FieldInfo target)
	{
		super(target.getName());
		this.target = target;
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
}
