package jadex.rules.eca;

import jadex.commons.future.IFuture;

/**
 *  Interface for the action part of a rule.
 */
public interface IAction<T>
{
	/**
	 *  Execute the action.
	 *  @param event The event.
	 *  @param rule The rule this action belongs to.
	 *  @param context The user context.
	 */
	public IFuture<T> execute(IEvent event, IRule<T> rule, Object context);
}
