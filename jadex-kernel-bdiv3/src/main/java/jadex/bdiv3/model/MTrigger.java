package jadex.bdiv3.model;

import java.util.ArrayList;
import java.util.List;



/**
 * 
 */
public class MTrigger
{
	protected List<MInternalEvent> internalevents;
	
	protected List<MMessageEvent> messageevents;
	
	protected List<MGoal> goals;
	
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
	
//	/**
//	 *  Get the fact added triggers (belief set names).
//	 */
//	public String[]	getFactAddeds();
//	
//	/**
//	 *  Get the fact added triggers (belief set names).
//	 */
//	public String[]	getFactRemoveds();
//	
//	/**
//	 *  Get the fact added triggers (belief set names).
//	 */
//	public String[]	getFactChangeds();

}
