package jadex.bdiv3.model;

import java.lang.reflect.Field;

/**
 *  Belief model.
 */
public class MBelief
{
	/** The target. */
	protected Field target;
	
	/**
	 *  Create a new belief.
	 */
	public MBelief(Field target)
	{
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
