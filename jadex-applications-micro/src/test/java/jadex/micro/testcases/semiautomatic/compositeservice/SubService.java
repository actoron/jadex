package jadex.micro.testcases.semiautomatic.compositeservice;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.BasicService;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Simple subtract service.
 */
public class SubService extends BasicService implements ISubService
{
	//-------- constructors --------
	
	/**
	 *  Create a new service.
	 */
	public SubService(IInternalAccess comp)
	{
		super(comp.getComponentIdentifier(), ISubService.class, null);
	}
	
	//-------- methods --------
	
	/**
	 *  Subtract two numbers.
	 *  @param a Number one.
	 *  @param b Number two.
	 *  @return The result of a minus b.
	 */
	public IFuture<Double> sub(double a, double b)
	{
		return new Future<Double>(Double.valueOf(a-b));
	}
}
