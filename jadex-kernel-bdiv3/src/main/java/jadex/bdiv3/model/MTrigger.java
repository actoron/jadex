package jadex.bdiv3.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



/**
 * 
 */
public class MTrigger
{
	protected List<MInternalEvent> internalevents;
	
	protected List<MMessageEvent> messageevents;
	
	protected List<MGoal> goals;
	
	protected List<String> factaddeds;
	
	protected List<String> factremoveds;
	
	protected List<String> factchangeds;
	
	/**
	 * 
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
	 * 
	 */
	public void addGoal(MGoal goal)
	{
		if(goals==null)
			this.goals = new ArrayList<MGoal>();
		goals.add(goal);
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
	 * 
	 */
	public void addMessageEvent(MMessageEvent event)
	{
		if(messageevents==null)
			this.messageevents = new ArrayList<MMessageEvent>();
		messageevents.add(event);
	}
	
	
//	/**
//	 *  Get the goal finished events.
//	 */
//	public IMTriggerReference[]	getGoalFinisheds();
	
	/**
	 * 
	 */
	public void addFactAdded(String fact)
	{
		if(factaddeds==null)
			this.factaddeds = new ArrayList<String>();
		factaddeds.add(fact);
	}
	
	/**
	 * 
	 */
	public void addFactRemoved(String fact)
	{
		if(factremoveds==null)
			this.factremoveds = new ArrayList<String>();
		factremoveds.add(fact);
	}
	
	/**
	 * 
	 */
	public void addFactChangeds(String fact)
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

}
