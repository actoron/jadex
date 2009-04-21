package jadex.rules.examples.manners;

import jadex.rules.rulesystem.IRule;

/**
 *  Interface for exchanging rule sets.
 *  Rule set implementations provide the same
 *  rules, but demonstrate the different rule languages.
 */
public interface IMannersRuleSet
{
	/**
	 *  Create rule "assign first seat". 
	 */
	public IRule createAssignFirstSeatRule();

	/**
	 *  Create find_seating rule.
	 */
	public IRule createFindSeatingRule();

	/**
	 *  Create rule "make path". 
	 */
	public IRule createMakePathRule();

	/**
	 *  Create rule "path done". 
	 */
	public IRule createPathDoneRule();

	/**
	 *  Create rule "we are done". 
	 */
	public IRule createAreWeDoneRule();

	/**
	 *  Create rule "continue". 
	 */
	public IRule createContinueRule();

	/**
	 *  Create rule "print results". 
	 */
	public IRule createPrintResultsRule();

	/**
	 *  Create rule "all done".
	 */
	public IRule createAllDoneRule();

}