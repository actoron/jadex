package jadex.bpmn.examples.service;

import jadex.commons.future.IFuture;

/**
 *  Service provided by the process.
 */
public interface ICalculatorService
{
	/**
	 *  Add two values.
	 */
	public IFuture	addValues(int a, int b);

	/**
	 *  Add three values.
	 */
	public IFuture	addValues(int a, int b, int c);

	/**
	 *  Subtract b from a.
	 *  @param a	The first value.
	 *  @param b	The value to subtract from the first.
	 */
	public IFuture	subtractValues(int a, int b);
}
