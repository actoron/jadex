package jadex.rules.rulesystem;

import java.util.List;

/**
 *  Interface for conditions.
 */
public interface ICondition
{
	/**
	 *  Get the variables.
	 *  @return The declared variables.
	 */
	public abstract List getVariables();
}
