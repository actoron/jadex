package jadex.bdiv3.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



/**
 *  Model element for a plan trigger.
 */
public class MTrigger
{
//	protected List<MInternalEvent> internalevents;
//	
//	protected List<MMessageEvent> messageevents;
	
	protected List<MGoal> goals;
	
	protected List<MGoal> goalfinisheds;
	
	protected List<String> factaddeds;
	
	protected List<String> factremoveds;
	
	protected List<String> factchangeds;
	
	protected List<MServiceCall> services;
	
	/**
	 *  Create a new trigger.
	 */
	public MTrigger()
	{
	}
	
//	/**
//	 *  Get the internal events.
//	 */
//	public List<MInternalEvent>	getInternalEvents()
//	{
//		return internalevents;
//	}
//	
//	/**
//	 *  Get the message events.
//	 */
//	public List<MMessageEvent> getMessageEvents()
//	{
//		return messageevents;
//	}
	
	/**
	 *  Get the goals.
	 */
	public List<MGoal> getGoals()
	{
		return goals;
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
	
//	/**
//	 * 
//	 */
//	public void addInternalEvent(MInternalEvent event)
//	{
//		if(internalevents==null)
//			this.internalevents = new ArrayList<MInternalEvent>();
//		internalevents.add(event);
//	}
//	
//	/**
//	 * 
//	 */
//	public void addMessageEvent(MMessageEvent event)
//	{
//		if(messageevents==null)
//			this.messageevents = new ArrayList<MMessageEvent>();
//		messageevents.add(event);
//	}
	
	
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
		fact = fact.replace(".", "/"); // Hack as long as capability separator is /
		factaddeds.add(fact);
	}
	
	/**
	 *  Add a fact removed belief trigger. 
	 */
	public void addFactRemoved(String fact)
	{
		if(factremoveds==null)
			this.factremoveds = new ArrayList<String>();
		fact = fact.replace(".", "/");
		factremoveds.add(fact);
	}
	
	/**
	 *  Add a fact changed belief trigger. 
	 */
	public void addFactChangeds(String fact)
	{
		if(factchangeds==null)
			this.factchangeds = new ArrayList<String>();
		fact = fact.replace(".", "/");
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
}
