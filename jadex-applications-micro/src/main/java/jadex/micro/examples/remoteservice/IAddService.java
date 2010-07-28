package jadex.micro.examples.remoteservice;

import jadex.commons.IFuture;
import jadex.service.IService;

/**
 *  Simple interface for a component service.
 */
public interface IAddService extends IService
{
	/**
	 *  Add two numbers.
	 *  @param a First number.
	 *  @param b Second number.
	 *  @return Future that will deliver the sum of a and b.
	 */
	public IFuture addNB(int a, int b);
	
	/**
	 *  Add two numbers.
	 *  @param a First number.
	 *  @param b Second number.
	 *  @return The sum of a and b.
	 */
	public int addB(int a, int b);
}
