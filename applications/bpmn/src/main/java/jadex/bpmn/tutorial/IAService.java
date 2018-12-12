package jadex.bpmn.tutorial;

import jadex.commons.future.IFuture;

/**
 * 
 */
public interface IAService
{
	/**
	 * 
	 */
	public IFuture<String> appendHello(String text);
}
