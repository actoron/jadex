package jadex.bdiv3.actions;

import jadex.bridge.IComponentStep;
import jadex.commons.future.IFuture;

/**
 *  The interface for interpreter actions. 
 */
public interface IAction<T> extends IComponentStep<T>
{
	/**
	 *  Test if the action is valid.
	 *  @return True, if action is valid.
	 */
	public IFuture<Boolean> isValid();

}
