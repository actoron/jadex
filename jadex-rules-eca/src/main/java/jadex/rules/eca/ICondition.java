package jadex.rules.eca;

/**
 *  Interface for a condition part of a rule.
 */
public interface ICondition
{
	/**
	 *  Evaluation the condition.
	 *  @param event The event.
	 *  @return True, if condition is met.
	 */
	public boolean evaluate(IEvent event);
}
