package jadex.micro.testcases.semiautomatic.compositeservice;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.IServiceIdentifier;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 * 
 */
public class PojoAddService implements IAddService
{
	// todo: make injectable these attribues
	
	/** The service identifier. */
	protected IServiceIdentifier sid;
	
	/** The service provider. */
	protected IInternalAccess comp;
	
	/**
	 *  Create the service.
	 */
	public PojoAddService(IInternalAccess comp)
	{
		this.comp = comp;
	}
	
	//-------- methods --------

	/**
	 *  Add two numbers.
	 *  @param a Number one.
	 *  @param b Number two.
	 *  @return The sum of a and b.
	 */
	public IFuture add(double a, double b)
	{
		System.out.println("add service called on: "+sid);
		return new Future(new Double(a+b));
	}
}
