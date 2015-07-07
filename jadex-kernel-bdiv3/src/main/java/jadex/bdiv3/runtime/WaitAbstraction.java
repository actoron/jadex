package jadex.bdiv3.runtime;

import jadex.bdiv3.model.MElement;
import jadex.bdiv3.model.MInternalEvent;
import jadex.bdiv3.model.MMessageEvent;
import jadex.bdiv3.runtime.impl.RElement;

import java.util.HashSet;
import java.util.Set;

/**
 *  Object that indicates on which elements a plan is waiting. 
 */
public class WaitAbstraction
{	
	/** The model elements. */
	protected Set<MElement> modelelements;
	
	/** The runtime elements. */
	protected Set<RElement> runtimeelements;

	/** The event types. */
	protected Set<String> changeeventtypes;
	
	/**
	 *  Add a message event.
	 *  @param type The type.
	 */
	public void addMessageEvent(MMessageEvent mevent)
	{
		addModelElement(mevent);
	}
	
//	/**
//	 *  Add a message event reply.
//	 *  @param me The message event.
//	 */
//	public void addReply(RMessageEvent mevent)
//	{
//		if(runtimeelements==null)
//		{
//			runtimeelements = new HashSet<RElement>();
//		}
//		runtimeelements.add(mevent);
//	}

	/**
	 *  Add an internal event.
	 *  @param type The type.
	 */
	public void addInternalEvent(MInternalEvent mevent)
	{
		addModelElement(mevent);
	}
	
//	/**
//	 *  Add a goal.
//	 *  @param type The type.
//	 */
//	public void addGoal(MGoal mgoal)
//	{
//		addModelElement(mgoal);
//	}
//
//	/**
//	 *  Add a goal.
//	 *  @param goal The goal.
//	 */
//	public void addGoal(RGoal rgoal)
//	{
//		addRuntimeElement(rgoal);
//	}

//	/**
//	 *  Add a fact changed.
//	 *  @param belief The belief or beliefset.
//	 */
//	public void addFactChanged(String belief)
//	{
//		addChangeEventType()
//	}
//
//	/**
//	 *  Add a fact added.
//	 *  @param beliefset The beliefset.
//	 */
//	public IWaitAbstraction addFactAdded(String beliefset)
//	{
//		
//	}


//	/**
//	 *  Add a fact removed.
//	 *  @param beliefset The beliefset.
//	 */
//	public IWaitAbstraction addFactRemoved(String beliefset)
//	{
//		
//	}
//	
//	/**
//	 *  Add a condition.
//	 *  @param condition the condition name.
//	 */
//	public IWaitAbstraction addCondition(String condition)
//	{
//		
//	}
//
//	/**
//	 *  Add an external condition.
//	 *  @param condition the condition.
//	 */
//	public IWaitAbstraction addExternalCondition(IExternalCondition condition)
//	{
//		
//	}

	//-------- remover methods --------

	/**
	 *  Remove a message event.
	 *  @param type The type.
	 */
	public void removeMessageEvent(MMessageEvent mevent)
	{
		removeModelElement(mevent);
	}

//	/**
//	 *  Remove a message event reply.
//	 *  @param me The message event.
//	 */
//	public void removeReply(IMessageEvent me)
//	{
//		
//	}

//	/**
//	 *  Remove an internal event.
//	 *  @param type The type.
//	 */
//	public void removeInternalEvent(MInternalEvent mevent)
//	{
//		removeModelElement(mevent);
//	}
//
//	/**
//	 *  Remove a goal.
//	 *  @param type The type.
//	 */
//	public void removeGoal(MGoal mgoal)
//	{
//		removeModelElement(mgoal);
//	}
//
//	/**
//	 *  Remove a goal.
//	 *  @param goal The goal.
//	 */
//	public void removeGoal(RGoal rgoal)
//	{
//		removeRuntimeElement(rgoal);
//	}
	
//	/**
//	 *  Remove a fact changed.
//	 *  @param belief The belief or beliefset.
//	 */
//	public void removeFactChanged(String belief);
//
//	/**
//	 *  Remove a fact added.
//	 *  @param beliefset The beliefset.
//	 */
//	public void removeFactAdded(String beliefset);
//
//
//	/**
//	 *  Remove a fact removed.
//	 *  @param beliefset The beliefset.
//	 */
//	public void removeFactRemoved(String beliefset);	
//
//	/**
//	 *  Remove a condition.
//	 *  @param condition the condition name.
//	 */
//	public void removeCondition(String condition);
//	
//	/**
//	 *  Remove an external condition.
//	 *  @param condition the condition.
//	 */
//	public void	removeExternalCondition(IExternalCondition condition);
	
	/**
	 * 
	 */
	protected void addModelElement(MElement melement)
	{
		if(melement==null)
			throw new IllegalArgumentException("Element must not null.");
		
		if(modelelements==null)
		{
			modelelements = new HashSet<MElement>();
		}
		modelelements.add(melement);
	}
	
	/**
	 * 
	 */
	protected void removeModelElement(MElement melement)
	{
		if(melement==null)
			throw new IllegalArgumentException("Element must not null.");
		
		if(modelelements!=null)
		{
			modelelements.remove(melement);
		}
	}
	
	/**
	 * 
	 */
	public void addRuntimeElement(RElement relement)
	{
		if(relement==null)
			throw new IllegalArgumentException("Element must not null.");
		
		if(runtimeelements==null)
		{
			runtimeelements = new HashSet<RElement>();
		}
		runtimeelements.add(relement);
	}
	
	/**
	 * 
	 */
	public void removeRuntimeElement(RElement relement)
	{
		if(relement==null)
			throw new IllegalArgumentException("Element must not null.");
		
		if(runtimeelements!=null)
		{
			runtimeelements.remove(relement);
		}
	}
	
	/**
	 * 
	 */
	public void addChangeEventType(String eventtype)
	{
		if(changeeventtypes==null)
		{
			changeeventtypes = new HashSet<String>();
		}
		changeeventtypes.add(eventtype);
	}
	
	/**
	 * 
	 */
	public void removeChangeEventType(String eventtype)
	{
		if(changeeventtypes!=null)
		{
			changeeventtypes.remove(eventtype);
		}
	}
	
	/**
	 *  Test if this wait abstraction is waiting for the element.
	 */
	public boolean isWaitingFor(Object procelem)
	{
		boolean ret = false;
		if(modelelements!=null)
		{
			if(procelem instanceof RElement)
			{
				ret = modelelements.contains(((RElement)procelem).getModelElement());
			}
			else
			{
				ret = modelelements.contains(procelem);
			}
		}
		if(!ret && runtimeelements!=null)
		{
			ret = runtimeelements.contains(procelem);
		}
		if(!ret && changeeventtypes!=null)
		{
			if(procelem instanceof ChangeEvent)
			{
				String type = ((ChangeEvent)procelem).getType();
				String src = (String)((ChangeEvent)procelem).getSource();
				ret = changeeventtypes.contains(type+"."+src);
			}
		}
		
		return ret;
	}

}
