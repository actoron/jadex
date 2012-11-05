package jadex.rules.eca;

/**
 *  Interface for the action part of a rule.
 */
public interface IAction
{
	/**
	 *  Execute the action.
	 *  @param event The event.
	 *  @param rule The rule this action belongs to.
	 *  @param context The user context.
	 */
	public void execute(IEvent event, IRule rule, Object context);
}
