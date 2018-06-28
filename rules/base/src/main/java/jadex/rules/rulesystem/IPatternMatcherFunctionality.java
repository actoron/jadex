package jadex.rules.rulesystem;

import jadex.rules.state.IOAVState;

/**
 *  Static part of a pattern matcher (can be shared among many states).
 */
public interface IPatternMatcherFunctionality extends Cloneable
{
	/**
	 *  Create an instance of the pattern matcher for a given state.
	 *  @param state The state.
	 *  @param agenda The agenda.
	 */
	public IPatternMatcherState createMatcherState(IOAVState state, AbstractAgenda agenda);
	
	/**
	 *  Get the rulebase.
	 *  @return The rulebase.
	 */
	public IRulebase getRulebase();
	
	/**
	 *  Clone this object.
	 *  @return A clone of this object.
	 */
	public Object clone();
}
