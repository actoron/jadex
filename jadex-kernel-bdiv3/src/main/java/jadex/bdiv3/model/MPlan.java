package jadex.bdiv3.model;

import java.lang.reflect.Method;

/**
 * 
 */
public class MPlan extends MElement
{
	/** The trigger. */
	protected MTrigger trigger;
	
	/** The plan body. */
	protected Object body;
	
	/** The plan priority. */
	protected int priority;
	
	/**
	 *  Create a new belief.
	 */
	public MPlan(String name, Object body, MTrigger trigger, int priority)
	{
		super(name);
		this.body = body;
		this.trigger = trigger;
		this.priority = priority;
	}

	/**
	 *  Get the body.
	 *  @return The body.
	 */
	public Object getBody()
	{
		return body;
	}

	/**
	 *  Set the body.
	 *  @param body The body to set.
	 */
	public void setBody(Object body)
	{
		this.body = body;
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
