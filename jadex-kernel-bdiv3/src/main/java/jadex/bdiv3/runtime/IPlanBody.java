package jadex.bdiv3.runtime;

import jadex.commons.future.IFuture;

/**
 *  Interface for plan body.
 */
public interface IPlanBody
{
	/**
	 * 
	 */
	public IFuture<Void> executePlanStep();
}
