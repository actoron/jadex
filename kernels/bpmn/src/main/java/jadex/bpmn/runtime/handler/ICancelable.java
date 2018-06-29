package jadex.bpmn.runtime.handler;

import jadex.commons.future.IFuture;

/**
 * 
 */
public interface ICancelable
{
	/**
	 * 
	 */
	public IFuture<Void> cancel();
}
