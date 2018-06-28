package jadex.platform.service.ecarules;

import jadex.bridge.service.types.ecarules.IRulebaseEvent;
import jadex.rules.eca.IRule;

/**
 * 
 */
public class RuleAddedEvent extends ARulebaseEvent implements IRulebaseEvent
{
	/** The rule. */
	protected IRule<?> rule;
	
	/**
	 *  Create a new rule added event.
	 */
	public RuleAddedEvent()
	{
	}
	
	/**
	 *  Create a new rule added event.
	 */
	public RuleAddedEvent(int callid, IRule<?> rule)
	{
		super(callid);
		this.rule = rule;
	}
	/**
	 *  Create a new rule added event.
	 */
	public RuleAddedEvent(RuleAddedEvent event)
	{
		super(event.getCallId());
		this.rule = event.getRule();
	}

	/**
	 *  Get the rule.
	 *  return The rule.
	 */
	public IRule<?> getRule()
	{
		return rule;
	}

	/**
	 *  Set the rule. 
	 *  @param rule The rule to set.
	 */
	public void setRule(IRule<?> rule)
	{
		this.rule = rule;
	}
	
	/**
	 *  Copy the object.
	 */
	public ARulebaseEvent createCopy()	
	{
		return new RuleAddedEvent(this);
	}
}
