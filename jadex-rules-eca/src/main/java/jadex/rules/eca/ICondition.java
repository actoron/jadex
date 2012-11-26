package jadex.rules.eca;

/**
 *  Interface for a condition part of a rule.
 */
public interface ICondition
{
	public static ICondition TRUE_CONDITION = new ICondition()
	{
		public boolean evaluate(IEvent event)
		{
			return true;
		}
	};
	
	/**
	 *  Evaluation the condition.
	 *  @param event The event.
	 *  @return True, if condition is met.
	 */
	public boolean evaluate(IEvent event);
}
