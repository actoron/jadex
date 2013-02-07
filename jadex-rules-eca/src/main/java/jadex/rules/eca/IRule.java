package jadex.rules.eca;

import java.util.List;

/**
 *  Interface for a rule.
 *  Has a 
 *  - name
 *  - event types it reacts to
 *  - condition, lhs of the rule
 *  - action, rhs of the rule
 */
public interface IRule<T>
{
	/**
	 *  Get the rule name.
	 *  @return The rule name.
	 */
	public String getName();

	/**
	 *  Get the event types this rule reactes to.
	 *  @return The event types.
	 */
	public List<EventType> getEvents();
	
	/**
	 *  Get the condition of the rule.
	 *  @return The condition.
	 */
	public ICondition getCondition();
	
	/**
	 *  Get the action of the rule.
	 *  @return The action.
	 */
	public IAction<T> getAction();

}
