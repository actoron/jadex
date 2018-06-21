package jadex.rules.rulesystem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


/**
 *  The agenda contains the activations and can use
 *  a conflict resolution strategy for deciding which
 *  activation to fire.
 */
public abstract class AbstractAgenda implements IAgenda
{
	//-------- attributes --------
	
	/** The last activation. Represents the last rule that has been
	  executed (or that is currently executing, if still running). */
	protected Activation	last;
		
	/** Agenda listeners (if any). */
	protected List	listeners;
	
	/** The history of executed activations (if enabled). */
	protected List	history;
	
	/** The state. */
	protected int state;
	
	//-------- constructors --------
	
	/**
	 *  Create a new fifo agenda.
	 *  @param state The state.
	 */
	public AbstractAgenda()
	{
	}
	
	//-------- IAgenda interface --------
	
	/**
	 *  Fire one activated rule.
	 */
	public void	fireRule()
	{
		last	= getNextActivation();
		if(last!=null)
		{
			if(history!=null)
				history.add(last.toString());

//			System.err.println("++++++++ executing: "+last);
//			if(last.toString().indexOf("listener_belief_changed")!=-1)
//				System.out.println("hhhhh");
			last.execute();
			
			// Hack!!! Shouldn't remove executed activations?
			removeActivation(last);
		}
	}

	
	//--------- rete methods --------
	
	/**
	 *  Add a new activation.
	 *  @param act The activation.
	 */
	public abstract void addActivation(Activation act);
	
	/**
	 *  Remove an activation
	 *  @param act The activation.
	 */
	public abstract void removeActivation(Activation act);
	
	/**
	 *  Get the current activations.
	 *  @return The activations.
	 */
	public abstract Collection getActivations();
	
	/**
	 *  Test if the agenda is empty.
	 *  @return True if is empty.
	 */
	public abstract boolean isEmpty();
	
	/**
	 *  Get the next activation.
	 *  @return The next activation.
	 */ 
	public abstract Activation getNextActivation();

	/**
	 *  Set the next activation.
	 */ 
	public abstract void setNextActivation(Activation next);
	
	/**
	 *  Get the state of the agenda.
	 *  Changes whenever the activation list changes.
	 */
	public int getState()
	{
		return state;
	}
	
	/**
	 *  The last activation. Represents the last rule that has been
	 *  executed (or that is currently executing, if still running).
	 *  @return null, when no rule has been executed or when fireRule
	 *    has been called on an empty agenda.	
	 */ 
	public Activation getLastActivation()
	{
		return last;
	}
	
	/**
	 *  Get the history mode.
	 */
	public boolean	isHistoryEnabled()
	{
		return history!=null;
	}
	
	/**
	 *  Set the history mode.
	 */
	public void	setHistoryEnabled(boolean enabled)
	{
		// Hack!!! synchronized because of AgendaPanel.
		if(enabled && history==null)
			history	= Collections.synchronizedList(new ArrayList());
		else if(!enabled && history!=null)
			history	= null;
	}

	/**
	 *  Get the history.
	 */
	public List	getHistory()
	{
		return history;
	}
	
	/**
	 *  Add an agenda listener.
	 *  @param listener The listener.
	 */
	public void	addAgendaListener(IAgendaListener listener)
	{
		if(listeners==null)
		{
			listeners	= new ArrayList();
		}
		listeners.add(listener);
	}
	
	/**
	 *  Remove an agenda listener.
	 *  @param listener The listener.
	 */
	public void	removeAgendaListener(IAgendaListener listener)
	{
		if(listeners!=null && listeners.remove(listener) && listeners.isEmpty())
		{
			listeners	= null;
		}
	}
	
	/**
	 *  Notify all listeners (if any).
	 */
	protected void	notifyListeners()
	{
		if(listeners!=null)
		{
			IAgendaListener[]	alist	= (IAgendaListener[])listeners.toArray(new IAgendaListener[listeners.size()]);
			for(int i=0; i<alist.length; i++)
				alist[i].agendaChanged();
		}
	}
	
	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		StringBuffer ret = new StringBuffer("Agenda: ");
		for(Iterator it=getActivations().iterator(); it.hasNext(); )
			ret.append(it.next()).append(", ");
		return ret.toString();
	}
}