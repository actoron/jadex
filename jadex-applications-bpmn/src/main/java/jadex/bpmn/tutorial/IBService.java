package jadex.bpmn.tutorial;

import jadex.commons.future.IFuture;

/**
 * 
 */
public interface IBService
{
	/**
	 * 
	 */
	public IFuture<Integer> add(int a, int b);
	
	/**
	 * 
	 */
	public IFuture<Integer> sub(int a, int b);
}
