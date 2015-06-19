package jadex.bdiv3x.runtime;

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
	public RInternalEvent(MInternalEvent modelelement, IInternalAccess agent)
	{
		super(modelelement, null, agent);
	}
}
