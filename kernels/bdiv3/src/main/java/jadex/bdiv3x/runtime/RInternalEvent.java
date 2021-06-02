package jadex.bdiv3x.runtime;

import jadex.bdiv3.model.MConfigParameterElement;
import jadex.bdiv3.model.MInternalEvent;
import jadex.bdiv3.runtime.impl.RProcessableElement;
import jadex.bridge.IInternalAccess;

/**
 *  The runtime internal event.
 */
public class RInternalEvent extends RProcessableElement implements IInternalEvent 
{
	//-------- constructors --------
	
	/**
	 *  Create a new runtime element.
	 */
	public RInternalEvent(MInternalEvent modelelement, IInternalAccess agent, MConfigParameterElement config)
	{
		super(modelelement, null, agent, null, config);
	}
	
	/**
	 *  Get the name of the element in the fetcher (e.g. $goal).
	 *  @return The element name in the fetcher name.
	 */
	public String getFetcherName()
	{
		return "$event";
	}

	/**
	 *  Check if the element is currently part of the agent's reasoning.
	 *  E.g. the bases are always adopted and all of their contents such as goals, plans and beliefs.
	 */
	public boolean	isAdopted()
	{
		// TODO: Is this right?
		return false;
	}
}
