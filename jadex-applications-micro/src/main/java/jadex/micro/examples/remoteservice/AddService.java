package jadex.micro.examples.remoteservice;

import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.service.BasicService;
import jadex.service.IServiceProvider;

/**
 *  Simple implementation of the add interface.
 */
public class AddService extends BasicService implements IAddService
{
	/**
	 *  Create a new add service.
	 */
	public AddService(IServiceProvider provider, String name)
	{
		super(BasicService.createServiceIdentifier(provider.getId(), name));
	}
	
	/**
	 *  Add two numbers.
	 *  @param a First number.
	 *  @param b Second number.
	 *  @return Future that will deliver the sum of a and b.
	 */
	public IFuture addNB(int a, int b)
	{
//		System.out.println("addNB: "+a+" "+b);
		return new Future(new Integer(a+b));
	}
	
	/**
	 *  Add two numbers.
	 *  @param a First number.
	 *  @param b Second number.
	 *  @return The sum of a and b.
	 */
	public int addB(int a, int b)
	{
//		System.out.println("addB: "+a+" "+b);
		return a+b;
	}
}
