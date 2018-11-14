package jadex.rules.rulesystem;


/**
 *  The state specific part of a pattern matcher.
 *  Internal interface to be used for implementing new pattern matchers.
 */
public interface IPatternMatcherState
{
	/**
	 *  Initialize the pattern matcher.
	 *  Called before the agenda is accessed
	 *  to perform any initialization, if necessary.
	 */
	public void init();

	/**
	 *  Get the agenda.
	 *  The agenda can only be accessed, after the rule system
	 *  has been initialized with {@link #init()}.
	 *  @return The agenda.
	 */
	public IAgenda getAgenda();
}
