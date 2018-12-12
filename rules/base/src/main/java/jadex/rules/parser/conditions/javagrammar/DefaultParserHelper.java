package jadex.rules.parser.conditions.javagrammar;

import java.util.List;

import jadex.rules.rulesystem.ICondition;
import jadex.rules.rulesystem.rules.Variable;
import jadex.rules.state.OAVObjectType;
import jadex.rules.state.OAVTypeModel;

/**
 *  Basic handler for context specific parsing issues.
 */
public class DefaultParserHelper implements IParserHelper
{
	//-------- attributes --------
	
	/** The build context. */
	protected BuildContext	context;
		
	//-------- constructors --------
	
	/**
	 *  Create a BDI parser helper.
	 *  @param condition	The predefined condition.
	 *  @param state	The state.
	 *  @param returnvar	The return value variable (if return value condition).
	 */
	public DefaultParserHelper(ICondition condition, OAVTypeModel tmodel)
	{
		this.context	= new BuildContext(condition, tmodel);
	}
	
	//-------- IParserHelper interface --------
	
	/**
	 *  Get a variable with a given name.
	 *  @param	name	The variable name.
	 *  @return The variable.
	 */
	public Variable	getVariable(String name)
	{
		Variable	ret	= context.getVariable(name);

		return ret;
	}

	/**
	 *  Add a variable.
	 *  @param var The variable.
	 */
	public void	addVariable(Variable var)
	{
		context.addVariable(var);
	}

	/**
	 *  Test, if a name refers to a pseudo variable (e.g. $beliefbase).
	 *  @param	name	The variable name.
	 *  @return True, if the name is a pseudo variable.
	 */
	public boolean	isPseudoVariable(String name)
	{
		return false;
	}

	/**
	 *  Get the conditions after parsing.
	 */
	public List	getConditions()
	{
		return context.getConditions();
	}

	/**
	 *	Get the build context.
	 */
	public BuildContext	getBuildContext()
	{
		return context;
	}
	
	/**
	 *	Get the replacement type for an object type in an existential declaration
	 *	E.g. when a flyweight should be replaced by the real state type
	 *  (IGoal $g instead of goal $g)
	 *  @param type	The type to be replaced.
	 *  @return a tuple containing the replacement type and the replacement value source (e.g. a function call recreating the flyweight from the state object).
	 */
	public Object[]	getReplacementType(OAVObjectType type)
	{
		return null;
	}
}
