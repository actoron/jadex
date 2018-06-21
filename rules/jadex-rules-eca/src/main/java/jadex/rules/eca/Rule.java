package jadex.rules.eca;

import java.util.ArrayList;
import java.util.List;

/**
 *  Implementation of a rule.
 *  Has a 
 *  - name
 *  - event types it reacts to
 *  - condition, lhs of the rule
 *  - action, rhs of the rule
 */
public class Rule<T> implements IRule<T>
{
	//-------- attributes --------
	
	/** The rule name. */
	protected String name;
	
	/** The event types. */
	protected List<EventType> events;
	
	/** The condition. */
	protected ICondition condition;
	
	/** The action. */
	protected IAction<T> action;

	//-------- constructors --------

	/**
	 *  Create a new rule.
	 */
	public Rule(String name)
	{
		this(name, null, null);
	}
	
	/**
	 *  Create a new rule.
	 */
	public Rule(String name, ICondition condition)
	{
		this(name, condition, null);
	}
	
	/**
	 *  Create a new rule.
	 */
	public Rule(String name, ICondition condition, IAction<T> action)
	{
		this(name, condition, action, null);
	}
	
	/**
	 *  Create a new rule.
	 */
	public Rule(String name, ICondition condition, IAction<T> action, EventType[] events)
	{
		this.name = name;
		this.condition = condition;
		this.action = action;
		if(events!=null)
		{
			for(EventType type: events)
				addEvent(type);
		}
	}

	//-------- methods --------
	
	/**
	 *  Get the rule name.
	 *  @return The rule name.
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 *  Set the name.
	 *  @param name The name to set.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 *  Get the condition.
	 *  @return The condition.
	 */
	public ICondition getCondition()
	{
		return condition;
	}
	
	/**
	 *  Set the condition.
	 *  @param condition The condition to set.
	 */
	public void setCondition(ICondition condition)
	{
		this.condition = condition;
	}

	/**
	 *  Get the action.
	 *  @return The action.
	 */
	public IAction<T> getAction()
	{
		return action;
	}

	/**
	 *  Set the action.
	 *  @param action The action to set.
	 */
	public void setAction(IAction<T> action)
	{
		this.action = action;
	}
	
	/**
	 *  Set the event types.
	 *  @param events The event types.
	 */
	public void setEvents(List<EventType> events)
	{
		this.events = events;
	}
	
	/**
	 *  Set the event types.
	 *  @param events The event types.
	 */
	public void setEventNames(List<String> events)
	{
		this.events = new ArrayList<EventType>();
		if(events!=null)
		{
			for(String ev: events)
			{
				this.events.add(new EventType(ev));
			}
		}
	}
	
	/**
	 *  Get the event types.
	 *  @retur The event types.
	 */
	public List<EventType> getEvents()
	{
		return events;
	}
	
	/**
	 *  Set the event types.
	 *  @param events The event types.
	 */
	public void addEvent(EventType event)
	{
		if(events==null)
			events = new ArrayList<EventType>();
		events.add(event);
	}

	/** 
	 * 
	 */
	public String toString()
	{
		return "Rule(name="+name+")";
	}
}
