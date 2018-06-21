package jadex.platform.service.ecarules;

import jadex.bridge.service.types.ecarules.IRulebaseEvent;


/**
 * 
 */
public class RuleRemovedEvent extends ARulebaseEvent implements IRulebaseEvent
{
	/** The rule name. */
	protected String rulename;
	
	/**
	 *  Create a new rule added event.
	 */
	public RuleRemovedEvent()
	{
	}
	
	/**
	 *  Create a new rule removed event.
	 */
	public RuleRemovedEvent(int callid, String rulename)
	{
		super(callid);
		this.rulename = rulename;
	}
	
	/**
	 *  Create a new rule removed event.
	 */
	public RuleRemovedEvent(RuleRemovedEvent event)
	{
		super(event.getCallId());
		this.rulename = event.getRuleName();
	}

	/**
	 *  Get the rulename.
	 *  return The rulename.
	 */
	public String getRuleName()
	{
		return rulename;
	}

	/**
	 *  Set the rulename. 
	 *  @param rulename The rulename to set.
	 */
	public void setRuleName(String rulename)
	{
		this.rulename = rulename;
	}
	
	/**
	 *  Copy the object.
	 */
	public ARulebaseEvent createCopy()	
	{
		return new RuleRemovedEvent(this);
	}
}
