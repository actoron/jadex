package jadex.bdiv3.runtime;

import java.util.ArrayList;
import java.util.List;

import jadex.bdiv3.model.MProcessableElement;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;

/**
 * 
 */
public abstract class RProcessableElement extends RElement
{
	/** The pojo element. */
	protected Object pojoelement;
	
	/** The applicable plan list. */
	protected APL apl;
	
	/** The tried plans. */
	protected List<Object> triedplans;
	
	/**
	 *  Create a new element.
	 */
	public RProcessableElement(MProcessableElement modelelement, Object pojoelement)
	{
		super(modelelement);
		this.pojoelement = pojoelement;
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
	 * 
	 */
	public void planFinished(IInternalAccess ia, RPlan rplan)
	{
		// potentially remove candidate
		addTriedPlan(rplan.getCandidate());
		apl.planFinished(rplan);
		// create reasoning step depending on the processable element type
		ia.getExternalAccess().scheduleStep(createReasoningStep(ia));
	}
	
	/**
	 * 
	 */
	public abstract IComponentStep<Void> createReasoningStep(IInternalAccess ia);
}