package jadex.bdiv3.model;

import java.lang.reflect.Method;

/**
 * 
 */
public class MPlan
{
	/** The trigger. */
	protected MTrigger trigger;
	
	/** The target. */
	protected Method target;
	
	/**
	 *  Create a new belief.
	 */
	public MPlan(Method target, MTrigger trigger)
	{
		this.target = target;
		this.trigger = trigger;
	}

	/**
	 *  Get the target.
	 *  @return The target.
	 */
	public Method getTarget()
	{
		return target;
	}

	/**
	 *  Set the target.
	 *  @param target The target to set.
	 */
	public void setTarget(Method target)
	{
		this.target = target;
	}

	/**
	 *  Get the trigger.
	 *  @return The trigger.
	 */
	public MTrigger getTrigger()
	{
		return trigger;
	}

	/**
	 *  Set the trigger.
	 *  @param trigger The trigger to set.
	 */
	public void setTrigger(MTrigger trigger)
	{
		this.trigger = trigger;
	}
}
