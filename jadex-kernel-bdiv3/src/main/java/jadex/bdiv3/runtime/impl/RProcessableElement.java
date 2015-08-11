package jadex.bdiv3.runtime.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jadex.bdiv3.actions.FindApplicableCandidatesAction;
import jadex.bdiv3.model.MConfigParameterElement;
import jadex.bdiv3.model.MProcessableElement;
import jadex.bridge.IInternalAccess;

/**
 *  Runtime element for all elements that can be processed via means-end reasoning.
 */
public abstract class RProcessableElement extends RParameterElement
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
	protected List<IInternalPlan> triedplans;
	
	/** The state. */
	protected State state;

	/**
	 *  Create a new element.
	 */
	public RProcessableElement(MProcessableElement modelelement, Object pojoelement, IInternalAccess agent, Map<String, Object> vals, MConfigParameterElement config)
	{
		super(modelelement, agent, vals, config);
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
//		if(apl==null)
//			System.out.println("set apl to null: "+this);
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
	public void addTriedPlan(IInternalPlan plan)
	{
		if(triedplans==null)
		{
			triedplans = new ArrayList<IInternalPlan>();
		}
		triedplans.add(plan);
	}
	
	/**
	 *  Get the triedplans.
	 *  @return The triedplans.
	 */
	public List<IInternalPlan> getTriedPlans()
	{
		return triedplans;
	}

	/**
	 *  Set the triedplans.
	 *  @param triedplans The triedplans to set.
	 */
	public void setTriedPlans(List<IInternalPlan> triedplans)
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
	 *  Set the state.
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
	 *  Called when plan execution has finished.
	 */
	public void planFinished(IInternalAccess ia, IInternalPlan rplan)
	{
		if(rplan!=null)
		{
			if(apl!=null)
			{
				// do not add tried plan if apl is already reset because procedural
				// goal semantics is wrong otherwise (isProceduralSucceeded)
				addTriedPlan(rplan);
				apl.planFinished(rplan);
			}
		}
	}
}