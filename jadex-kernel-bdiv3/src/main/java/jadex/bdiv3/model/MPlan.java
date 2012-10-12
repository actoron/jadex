package jadex.bdiv3.model;

import java.lang.reflect.Method;

/**
 * 
 */
public class MPlan extends MElement
{
	/** The trigger. */
	protected MTrigger trigger;
	
	/** The target. */
	protected Method target;
	
	/** The plan priority. */
	protected int priority;
	
	/**
	 *  Create a new belief.
	 */
	public MPlan(Method target, MTrigger trigger, int priority)
	{
		super(target.getName());
		this.target = target;
		this.trigger = trigger;
		this.priority = priority;
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

	/**
	 *  Get the priority.
	 *  @return The priority.
	 */
	public int getPriority()
	{
		return priority;
	}

	/**
	 *  Set the priority.
	 *  @param priority The priority to set.
	 */
	public void setPriority(int priority)
	{
		this.priority = priority;
	}
}
