package jadex.micro.testcases.semiautomatic.remoteservice;

import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.service.BasicService;
import jadex.commons.service.IServiceProvider;

/**
 *  Simple implementation of the math interface.
 */
public class MathService extends BasicService implements IMathService
{
	//-------- constructors --------
	
	/**
	 *  Create a new add service.
	 */
	public MathService(IServiceProvider provider)
	{
		super(provider.getId(), IMathService.class, null);
	}
	
	//-------- methods --------
	
	/**
	 *  Tests a non-blocking call.
	 * 
	 *  Add two numbers.
	 *  @param a First number.
	 *  @param b Second number.
	 *  @return Future that will deliver the sum of a and b.
	 */
	public IFuture addNB(int a, int b)
	{
		System.out.println("addNB: "+a+" "+b);
		return new Future(new Integer(a+b));
	}
	
	/**
	 *  Tests a blocking call (should be avoided!).
	 * 
	 *  Add two numbers.
	 *  @param a First number.
	 *  @param b Second number.
	 *  @return The sum of a and b.
	 */
	public int addB(int a, int b)
	{
		System.out.println("addB: "+a+" "+b);
		return a+b;
	}
	
	/**
	 *  Tests a constant call, i.e. call without 
	 *  parameters are assumed to be constant so that
	 *  their value can be cached on local side.
	 *   
	 *  Get the PI value.
	 */
	public double getPi()
	{
		System.out.println("getPi");
		return Math.PI;
	}
	
	/**
	 *  Print out some message.
	 *  @param message The message.
	 */
	public void printMessage(String message)
	{
		System.out.println(message);
	}
	
	/**
	 *  Tests a method that throws an exception.
	 *  
	 *  Does a divide by zero operation and causes an exception.
	 */
	public IFuture divZero()
	{
		final Future ret = new Future();
		try
		{
			int tmp = 1/0;
			ret.setResult(new Integer(tmp));
		}
		catch(Exception e)
		{
			ret.setException(e);
		}
		return ret;
	}
}
