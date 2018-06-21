package jadex.rules.parser.conditions.javagrammar;

import java.util.List;

import jadex.rules.rulesystem.rules.Variable;
import jadex.rules.state.OAVObjectType;

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
	
	/**
	 *	Get the replacement type for an object type in an existential declaration
	 *	E.g. when a flyweight should be replaced by the real state type
	 *  (IGoal $g instead of goal $g)
	 *  Returns null when no replacement is required.
	 *  @param type	The type to be replaced.
	 *  @return a tuple containing the replacement type
	 *  and the replacement value source
	 *  (e.g. a function call recreating the flyweight from the state object)
	 *  or null for no replacement.
	 */
	public Object[]	getReplacementType(OAVObjectType type);
}
