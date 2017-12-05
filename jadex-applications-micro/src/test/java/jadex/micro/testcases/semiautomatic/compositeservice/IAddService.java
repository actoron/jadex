package jadex.micro.testcases.semiautomatic.compositeservice;

import jadex.commons.future.IFuture;

/**
 *  Interface for the add service.
 */
public interface IAddService
{
	/**
	 *  Add two numbers.
	 *  @param a Number one.
	 *  @param b Number two.
	 *  @return The sum of a and b.
	 */
	public IFuture<Double> add(double a, double b);
}
