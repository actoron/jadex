package jadex.bdiv3.runtime;

import jadex.bdiv3.actions.FindApplicableCandidatesAction;
import jadex.bdiv3.model.MProcessableElement;
import jadex.bridge.IInternalAccess;
import jadex.commons.SUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 
 */
public abstract class RProcessableElement extends RElement
{
	/** The processable element state initial. */
	public static final String	PROCESSABLEELEMENT_INITIAL	= "initial";
	
	/** The processable element state unprocessed. */
	public static final String	PROCESSABLEELEMENT_UNPROCESSED	= "unprocessed";

	/** The processable element state apl available. */
	public static final String	PROCESSABLEELEMENT_APLAVAILABLE	= "aplavailable";
	
	/** The processable element state meta-level reasoning. */
	public static final String	PROCESSABLEELEMENT_METALEVELREASONING	= "metalevelreasoning";
	
	/** The processable element state no candidates. */
	public static final String	PROCESSABLEELEMENT_NOCANDIDATES	= "nocandidates";

	/** The processable element state candidate selected. */
	public static final String	PROCESSABLEELEMENT_CANDIDATESSELECTED	= "candidatesselected";

	public static Set<String> PROCESSABLEELEMENT_STATES = SUtil.createHashSet(new String[]
	{
		PROCESSABLEELEMENT_INITIAL,
		PROCESSABLEELEMENT_UNPROCESSED,
		PROCESSABLEELEMENT_APLAVAILABLE,
		PROCESSABLEELEMENT_METALEVELREASONING,
		PROCESSABLEELEMENT_NOCANDIDATES,
		PROCESSABLEELEMENT_CANDIDATESSELECTED,
	});
	
	/** The pojo element. */
	protected Object pojoelement;
	
	/** The applicable plan list. */
	protected APL apl;
	
	/** The tried plans. */
	protected List<Object> triedplans;
	
	/** The state. */
	protected String state;
	
	/**
	 *  Create a new element.
	 */
	public RProcessableElement(MProcessableElement modelelement, Object pojoelement)
	{
		super(modelelement);
		this.pojoelement = pojoelement;
//		this.state = PROCESSABLEELEMENT_UNPROCESSED;
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
	public void addTriedPlan(Object plan)
	{
		if(triedplans==null)
		{
			triedplans = new ArrayList<Object>();
		}
		triedplans.add(plan);
	}
	
	/**
	 *  Get the triedplans.
	 *  @return The triedplans.
	 */
	public List<Object> getTriedPlans()
	{
		return triedplans;
	}

	/**
	 *  Set the triedplans.
	 *  @param triedplans The triedplans to set.
	 */
	public void setTriedPlans(List<Object> triedplans)
	{
		this.triedplans = triedplans;
	}

	/**
	 *  Get the state.
	 *  @return The state.
	 */
	public String getState()
	{
		return state;
	}

	/**
	 *  Set the state.
	 *  @param state The state to set.
	 */
	public void setState(String state)
	{
		this.state = state;
	}
	
	/**
	 * 
	 */
	public void setState(IInternalAccess ia, String state)
	{
		if(!PROCESSABLEELEMENT_STATES.contains(state))
			throw new IllegalArgumentException("Invalid state: "+state);
			
		setState(state);
		
		// start MR when state gets to unprocessed
		if(PROCESSABLEELEMENT_UNPROCESSED.equals(state))
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
			// potentially remove candidate
			addTriedPlan(rplan.getCandidate());
			apl.planFinished(rplan);
		}
	}
}