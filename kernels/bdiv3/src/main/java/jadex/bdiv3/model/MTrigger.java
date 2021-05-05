package jadex.bdiv3.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jadex.bridge.modelinfo.UnparsedExpression;

/**
 *  Model element for a plan trigger.
 */
public class MTrigger
{
	protected List<MInternalEvent> internalevents;
	
	/** The message events. */
	protected List<MMessageEvent> messageevents;
	
	/** The goal types. */
	protected List<MGoal> goals;
	
	/** Goal match expressions that restrict general goal type triggers. */
	protected Map<String, UnparsedExpression> goalmatches;
	
	/** The goal types of finished goals. */
	protected List<MGoal> goalfinisheds;
	
	/** The belief names. */
	protected List<String> factaddeds;
	
	/** The belief names. */
	protected List<String> factremoveds;
	
	/** The belief names. */
	protected List<String> factchangeds;
	
	/** The service types. */
	protected List<MServiceCall> services;
	
	//-------- additional xml properties --------
	
	// hack!!! required for two pass reading.
	protected List<String> messagenames;
	protected List<String> ieventnames;
	protected List<String> goalnames;
	protected List<String> goalfinishednames;
	protected Map<String, UnparsedExpression> goalmatchnames;
	
	/** The trigger condition. */
	protected MCondition condition;
	
	/**
	 *  Create a new trigger.
	 */
	public MTrigger()
	{
	}
	
	/**
	 *  Get the internal events.
	 */
	public List<MInternalEvent>	getInternalEvents()
	{
		return internalevents;
	}

	/**
	 *  Get the message events.
	 */
	public List<MMessageEvent> getMessageEvents()
	{
		return messageevents;
	}
	
	/**
	 *  Get the goals.
	 */
	public List<MGoal> getGoals()
	{
		return goals;
	}
	
	/**
	 *  Set the goals.
	 *  @param goals The goals to set
	 */
	public void setGoals(List<MGoal> goals)
	{
		this.goals = goals;
	}

	/**
	 *  Add a goal trigger.
	 */
	public void addGoal(MGoal goal)
	{
		if(goals==null)
			this.goals = new ArrayList<MGoal>();
		goals.add(goal);
	}
	
	/**
	 *  Get the goalfinisheds.
	 *  @return The goalfinisheds.
	 */
	public List<MGoal> getGoalFinisheds()
	{
		return goalfinisheds;
	}
	
	/**
	 *  Add a goal finished trigger.
	 */
	public void addGoalFinished(MGoal goal)
	{
		if(goalfinisheds==null)
			this.goalfinisheds = new ArrayList<MGoal>();
		goalfinisheds.add(goal);
	}
	
	/**
	 * 
	 */
	public void addInternalEvent(MInternalEvent event)
	{
		if(internalevents==null)
			this.internalevents = new ArrayList<MInternalEvent>();
		internalevents.add(event);
	}
	
	/**
	 *  Add a message event.
	 */
	public void addMessageEvent(MMessageEvent event)
	{
		if(messageevents==null)
			this.messageevents = new ArrayList<MMessageEvent>();
		messageevents.add(event);
	}
	
	/**
	 *  Add a message event name.
	 */
	public void addMessageName(String event)
	{
		if(messagenames==null)
			this.messagenames = new ArrayList<String>();
		messagenames.add(event);
	}
	
	/**
	 *  Get the message events.
	 */
	public List<String> getMessageNames()
	{
		return messagenames;
	}
	
	/**
	 *  Add an internal event name.
	 */
	public void addInternalEventName(String event)
	{
		if(ieventnames==null)
			this.ieventnames = new ArrayList<String>();
		ieventnames.add(event);
	}
	
	/**
	 *  Get the message events.
	 */
	public List<String> getInternalEventNames()
	{
		return ieventnames;
	}

	/**
	 *  Add a goal finished name.
	 */
	public void addGoalFinishedName(String event)
	{
		if(goalfinishednames==null)
			this.goalfinishednames = new ArrayList<String>();
		goalfinishednames.add(event);
	}
	
	/**
	 *  Get the goal finished events.
	 */
	public List<String> getGoalFinishedNames()
	{
		return goalfinishednames;
	}

	/**
	 *  Add a goal name.
	 */
	public void addGoalName(String event)
	{
		if(goalnames==null)
			this.goalnames = new ArrayList<String>();
		goalnames.add(event);
	}
	
	/**
	 *  Get the goal events.
	 */
	public List<String> getGoalNames()
	{
		return goalnames;
	}
	
	/**
	 *  Set the goalnames.
	 *  @param goalnames The goalnames to set
	 */
	public void setGoalNames(List<String> goalnames)
	{
		this.goalnames = goalnames;
	}

	/**
	 *  Add a goal match before preprecessing (i.e. unresolved name).
	 */
	public void addGoalNameMatchExpression(String goalname, UnparsedExpression match)
	{
		if(goalmatchnames==null)
			this.goalmatchnames = new HashMap<String, UnparsedExpression>();
		goalmatchnames.put(goalname, match);
	}
	
	/**
	 *  Get goal match expressions for preprecessing.
	 */
	public Map<String, UnparsedExpression> getGoalNameMatchExpressions()
	{
		return goalmatchnames;
	}
	
	/**
	 *  Add a goal name.
	 */
	public void addGoalMatchExpression(String goalname, UnparsedExpression match)
	{
		if(goalmatches==null)
			this.goalmatches = new HashMap<String, UnparsedExpression>();
		goalmatches.put(goalname, match);
	}
	
	/**
	 *  Get a goal match expression.
	 */
	public UnparsedExpression getGoalMatchExpression(MGoal mgoal)
	{
		return goalmatches==null? null: goalmatches.get(mgoal.getName());
	}

//	/**
//	 *  Get the goal finished events.
//	 */
//	public IMTriggerReference[]	getGoalFinisheds();

	/**
	 *  Add a fact added belief trigger. 
	 */
	public void addFactAdded(String fact)
	{
		if(factaddeds==null)
			this.factaddeds = new ArrayList<String>();
		factaddeds.add(fact);
	}
	
	/**
	 *  Add a fact removed belief trigger. 
	 */
	public void addFactRemoved(String fact)
	{
		if(factremoveds==null)
			this.factremoveds = new ArrayList<String>();
		factremoveds.add(fact);
	}
	
	/**
	 *  Add a fact changed belief trigger. 
	 */
	public void addFactChanged(String fact)
	{
		if(factchangeds==null)
			this.factchangeds = new ArrayList<String>();
		factchangeds.add(fact);
	}
	
	/**
	 *  Get the fact added triggers (belief set names).
	 */
	public List<String>	getFactAddeds()
	{
		return factaddeds==null? Collections.EMPTY_LIST: factaddeds;
	}
	
	/**
	 *  Get the fact removed triggers (belief set names).
	 */
	public List<String>	getFactRemoveds()
	{
		return factremoveds==null? Collections.EMPTY_LIST: factremoveds;
	}
	
	/**
	 *  Get the fact changeds triggers (belief set names).
	 */
	public List<String>	getFactChangeds()
	{
		return factchangeds==null? Collections.EMPTY_LIST: factchangeds;
	}
	
	/**
	 *  Set the fact added triggers (belief set names).
	 */
	public void	setFactAddeds(List<String> events)
	{
		this.factaddeds	= events;
	}
	
	/**
	 *  Set the fact removed triggers (belief set names).
	 */
	public void	setFactRemoveds(List<String> events)
	{
		this.factremoveds	= events;
	}
	
	/**
	 *  Set the fact changeds triggers (belief set names).
	 */
	public void	setFactChangeds(List<String> events)
	{
		this.factchangeds	= events;
	}
	
	/**
	 *  Add a service trigger.
	 */
	public void addService(MServiceCall service)
	{
		if(services==null)
			this.services = new ArrayList<MServiceCall>();
		services.add(service);
	}
	
	/**
	 *  Get the fact service calls.
	 */
	public List<MServiceCall>	getServices()
	{
		return services==null? Collections.EMPTY_LIST: services;
	}
	
	/**
	 *  Get the condition.
	 */
	public MCondition getCondition()
	{
		return condition;
	}
	
	/**
	 *  Set the condition.
	 */
	public void setCondition(MCondition condition)
	{
		this.condition = condition;
	}
}
