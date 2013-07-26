package jadex.bdiv3.runtime.impl;

import jadex.bdiv3.actions.FindApplicableCandidatesAction;
import jadex.bdiv3.model.MProcessableElement;
import jadex.bdiv3.runtime.impl.RGoal.GoalProcessingState;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.IResultListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public abstract class RProcessableElement extends RElement
{
	/** The allowed states. */
	public static enum State
	{
		INITIAL, 
		UNPROCESSED,
		APLAVAILABLE,
		METALEVELREASONING,
		NOCANDIDATES,
		CANDIDATESSELECTED
	};
	
	/** The pojo element. */
	protected Object pojoelement;
	
	/** The applicable plan list. */
	protected APL apl;
	
	/** The tried plans. */
	protected List<RPlan> triedplans;
	
	/** The state. */
	protected State state;

	/** The exception. */
	protected Exception exception;
	
	/** The listeners. */
	protected List<IResultListener<Void>>	listeners;
	
	/**
	 *  Create a new element.
	 */
	public RProcessableElement(MProcessableElement modelelement, Object pojoelement)
	{
		super(modelelement);
		this.pojoelement = pojoelement;
		this.state = State.INITIAL;
	}

	/**
	 *  Get the apl.
	 *  @return The apl.
	 */
	public APL getApplicablePlanList()
	{
		if(apl==null)
			apl = new APL(this);
		return apl;
	}

	/**
	 *  Set the apl.
	 *  @param apl The apl to set.
	 */
	public void setApplicablePlanList(APL apl)
	{
		this.apl = apl;
	}

	/**
	 *  Get the pojoelement.
	 *  @return The pojoelement.
	 */
	public Object getPojoElement()
	{
		return pojoelement;
	}

	/**
	 *  Set the pojoelement.
	 *  @param pojoelement The pojoelement to set.
	 */
	public void setPojoElement(Object pojoelement)
	{
		this.pojoelement = pojoelement;
	}
	
	/**
	 *  Add a tried plan.
	 */
	public void addTriedPlan(RPlan plan)
	{
		if(triedplans==null)
		{
			triedplans = new ArrayList<RPlan>();
		}
		triedplans.add(plan);
	}
	
	/**
	 *  Get the triedplans.
	 *  @return The triedplans.
	 */
	public List<RPlan> getTriedPlans()
	{
		return triedplans;
	}

	/**
	 *  Set the triedplans.
	 *  @param triedplans The triedplans to set.
	 */
	public void setTriedPlans(List<RPlan> triedplans)
	{
		this.triedplans = triedplans;
	}

	/**
	 *  Get the state.
	 *  @return The state.
	 */
	public State getState()
	{
		return state;
	}

	/**
	 *  Set the state.
	 *  @param state The state to set.
	 */
	public void setState(State state)
	{
		this.state = state;
	}
	
	/**
	 * 
	 */
	public void setState(IInternalAccess ia, State state)
	{
		if(getState().equals(state))
			return;
			
		setState(state);
		
		// start MR when state gets to unprocessed
		if(State.UNPROCESSED.equals(state))
		{
			ia.getExternalAccess().scheduleStep(new FindApplicableCandidatesAction(this));
		}
//		else if(PROCESSABLEELEMENT_APLAVAILABLE.equals(state))
//		{
//			ia.getExternalAccess().scheduleStep(new SelectCandidatesAction(this));
//		}
//		else if(PROCESSABLEELEMENT_CANDIDATESSELECTED.equals(state))
//		{
//			ia.getExternalAccess().scheduleStep(new ExecutePlanStepAction(this, rplan));
//		}
//		else if(PROCESSABLEELEMENT_NOCANDIDATES.equals(state))
//		{
//			
//		}
//		PROCESSABLEELEMENT_METALEVELREASONING
		
	}

	/**
	 * 
	 */
	public void planFinished(IInternalAccess ia, RPlan rplan)
	{
		if(rplan!=null)
		{
			addTriedPlan(rplan);
			if(apl!=null)
				apl.planFinished(rplan);
		}
	}

	/**
	 *  Add a new listener.
	 */
	public void addListener(IResultListener<Void> listener)
	{
		if(listeners==null)
			listeners = new ArrayList<IResultListener<Void>>();
		
		if(isSucceeded())
		{
			listener.resultAvailable(null);
		}
		else if(isFailed())
		{
			listener.exceptionOccurred(exception);
		}
		else
		{
			listeners.add(listener);
		}
	}

	/**
	 *  Remove a listener.
	 */
	public void removeListener(IResultListener<Void> listener)
	{
		if(listeners!=null)
			listeners.remove(listener);
	}
	
	/**
	 *  Get the listeners.
	 *  @return The listeners.
	 */
	public List<IResultListener<Void>> getListeners()
	{
		return listeners;
	}

	/**
	 *  Get the exception.
	 *  @return The exception.
	 */
	public Exception getException()
	{
		return exception;
	}

	/**
	 *  Set the exception.
	 *  @param exception The exception to set.
	 */
	public void setException(Exception exception)
	{
		this.exception = exception;
	}
	
	/**
	 *  Notify the listeners.
	 */
	public void notifyListeners()
	{
		if(getListeners()!=null)
		{
			for(IResultListener<Void> lis: getListeners())
			{
				if(isSucceeded())
				{
					lis.resultAvailable(null);
				}
				else if(isFailed())
				{
					lis.exceptionOccurred(exception);
				}
			}
		}
	}
	
	/**
	 *  Test if element is succeeded.
	 */
	public abstract boolean isSucceeded();
	
	/**
	 *  Test if element is failed.
	 */
	public abstract boolean isFailed();
}