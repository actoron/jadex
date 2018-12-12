package jadex.rules.eca;

/**
 *  Indicates a rule action intermediate result.
 */
public class RuleIntermediateEvent extends RuleEvent
{
	/**
	 *  Create a new RuleIntermediateEvent.
	 */
	public RuleIntermediateEvent()
	{
		super();
	}

	/**
	 *  Create a new RuleIntermediateEvent.
	 */
	public RuleIntermediateEvent(String rulename, Object result)
	{
		super(rulename, result);
	}
}
