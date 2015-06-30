package jadex.bdiv3x.runtime;

import jadex.bdiv3.model.MInternalEvent;
import jadex.bdiv3.runtime.impl.RPlan;
import jadex.bdiv3.runtime.impl.RProcessableElement;
import jadex.bridge.IInternalAccess;
import jadex.commons.IValueFetcher;

/**
 *  The runtime internal event.
 */
public class RInternalEvent extends RProcessableElement implements IInternalEvent 
{
	//-------- constructors --------
	
	/**
	 *  Create a new runtime element.
	 */
	public RInternalEvent(MInternalEvent modelelement, IInternalAccess agent)
	{
		super(modelelement, null, agent);
	}
	
	/**
	 *  Get the name of the element in the fetcher (e.g. $goal).
	 *  @return The element name in the fetcher name.
	 */
	public String getFetcherName()
	{
		return "$event";
	}
}
