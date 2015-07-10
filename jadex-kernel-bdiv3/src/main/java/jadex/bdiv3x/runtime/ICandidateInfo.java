package jadex.bdiv3x.runtime;

import jadex.bdiv3.runtime.IPlan;

/**
 *  The info objects for plan candidates.
 */
public interface ICandidateInfo
{
//	/**
//	 *  Get the plan instance.
//	 *  @return	The plan instance.
//	 */
//	public MPlanInfo getPlanInfo();
	
	/**
	 *  Get the plan instance.
	 *  @return	The plan instance.
	 */
	public IPlan getPlan();

	/**
	 *  Get the element this 
	 *  candidate was selected for.
	 *  @return	The processable element.
	 */
	public IElement getElement();

}
