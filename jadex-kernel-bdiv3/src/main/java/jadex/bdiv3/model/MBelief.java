package jadex.bdiv3.model;

import java.lang.reflect.Field;

/**
 *  Belief model.
 */
public class MBelief extends MElement
{
	/** The target. */
	protected Field target;
	
	/**
	 *  Create a new belief.
	 */
	public MBelief(Field target)
	{
		super(target.getName());
		this.target = target;
	}

	/**
	 *  Get the target.
	 *  @return The target.
	 */
	public Field getTarget()
	{
		return target;
	}

	/**
	 *  Set the target.
	 *  @param target The target to set.
	 */
	public void setTarget(Field target)
	{
		this.target = target;
	}
}
