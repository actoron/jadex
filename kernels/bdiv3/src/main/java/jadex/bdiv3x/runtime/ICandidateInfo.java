package jadex.bdiv3x.runtime;

import jadex.bdiv3.model.MElement;
import jadex.bdiv3.runtime.impl.IInternalPlan;

/**
 *  The info objects for plan candidates.
 */
public interface ICandidateInfo
{
//	public enum CandidateType
//	{
//		MPlanInfo, MGoalInfo, RPLan, Waitqueue, PojoPlan
//	}
	
//	/**
//	 *  Get the plan instance.
//	 *  @return	The plan instance.
//	 */
//	public MPlanInfo getPlanInfo();
	
	/**
	 *  Get the plan instance.
	 *  @return	The plan instance.
	 */
	public IInternalPlan getPlan();
	
	/**
	 *  Get the candidate model element.
	 *  @return The candiate model element.
	 */
//	public MPlan getMPlan();
	public MElement getModelElement();
	
	/**
	 *  Get the element this 
	 *  candidate was selected for.
	 *  @return	The processable element.
	 */
	public IElement getElement();
	
	/**
	 *  Get the raw candidate.
	 *  @return The raw candiate.
	 */
	public Object getRawCandidate();
	
}
