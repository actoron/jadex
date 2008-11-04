package jadex.bdi.runtime;

/**
 *  The info objects for plan candidates.
 */
public interface ICandidateInfo
{
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
