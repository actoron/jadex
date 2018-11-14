package jadex.micro.testcases.semiautomatic.compositeservice;

import jadex.commons.future.IFuture;

/**
 *  Simple subtract service.
 */
public interface ISubService
{
	/**
	 *  Subtract two numbers.
	 *  @param a Number one.
	 *  @param b Number two.
	 *  @return The result of a minus b.
	 */
	public IFuture sub(double a, double b);
}
