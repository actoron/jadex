package jadex.rules.parser.conditions.javagrammar;

import jadex.rules.rulesystem.rules.Variable;

import java.util.List;

/**
 *  Provides an extension point to introduce
 *  additional information (e.g. local variables)
 *  in the parsing process.  
 */
public interface IParserHelper
{
	/**
	 *  Get a variable with a given name.
	 *  @param	name	The variable name.
	 *  @return The variable.
	 */
	public Variable	getVariable(String name);

	/**
	 *  Add a variable.
	 *  @param var The variable.
	 */
	public void	addVariable(Variable var);

	/**
	 *  Test, if a name refers to a pseudo variable (e.g. $beliefbase).
	 *  @param	name	The variable name.
	 *  @return True, if the name is a pseudo variable.
	 */
	public boolean	isPseudoVariable(String name);

	/**
	 *  Get the conditions after parsing.
	 */
	public List	getConditions();
	
	/**
	 *	Get the build context.
	 */
	public BuildContext	getBuildContext();
}
