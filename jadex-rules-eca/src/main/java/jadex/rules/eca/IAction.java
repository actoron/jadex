package jadex.rules.eca;

/**
 *  Interface for the action part of a rule.
 */
public interface IAction
{
	/**
	 *  Execute the action.
	 *  @param event The event.
	 */
	public void execute(IEvent event);
}
