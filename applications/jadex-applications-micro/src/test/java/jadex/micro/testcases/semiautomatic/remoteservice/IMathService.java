package jadex.micro.testcases.semiautomatic.remoteservice;

import jadex.bridge.service.IService;
import jadex.commons.future.IFuture;

/**
 *  Simple interface for a component service.
 */
//@Timeout(4321)
public interface IMathService extends IService
{
	/**
	 *  Tests a non-blocking call.
	 * 
	 *  Add two numbers.
	 *  @param a First number.
	 *  @param b Second number.
	 *  @return Future that will deliver the sum of a and b.
	 */
//	@Timeout(1234567)
	public IFuture<Integer> addNB(int a, int b);
	
	/**
	 *  Tests a blocking call (should be avoided!).
	 * 
	 *  Add two numbers.
	 *  @param a First number.
	 *  @param b Second number.
	 *  @return The sum of a and b.
	 */
//	@Replacement("jadex.micro.examples.remoteservice.IMathService$AddBReplacement")
	public int addB(int a, int b);
	
//	public static class AddBReplacement	implements IMethodReplacement
//	{
//		public Object invoke(Object obj, Object[] args)
//		{
//			System.out.println("replaced: "+obj+", "+SUtil.arrayToString(args));
//			return Integer.valueOf(((Number)args[0]).intValue() + ((Number)args[1]).intValue());
//		}
//	}
	
	/**
	 *  Tests a constant call, i.e. call without 
	 *  parameters are assumed to be constant so that
	 *  their value can be cached on local side.
	 *   
	 *  Get the PI value.
	 */
//	@Uncached @Timeout(100)
	public double getPi();
	
	/**
	 *  Tests a void message call (default is asynchronous).
	 * 
	 *  Print out some message.
	 *  @param message The message.
	 */
//	@Synchronous
	public void printMessage(String message);
	
	/**
	 *  Tests a method that throws an exception.
	 *  
	 *  Does a divide by zero operation and causes an exception.
	 */
//	@Excluded
	public IFuture<Void> divZero();
}
