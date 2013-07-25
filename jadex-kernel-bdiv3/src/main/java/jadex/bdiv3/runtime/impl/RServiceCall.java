package jadex.bdiv3.runtime.impl;

import jadex.bdiv3.model.MProcessableElement;

/**
 * 
 */
public class RServiceCall extends RProcessableElement
{
	/**
	 *  Create a new ServiceCall. 
	 */
	public RServiceCall(MProcessableElement modelelement, InvocationInfo pojoelement)
	{
		super(modelelement, pojoelement);
	}
	
	/**
	 *  Get the invocation info.
	 */
	public InvocationInfo getInvocationInfo()
	{
		return (InvocationInfo)getPojoElement();
	}
}

