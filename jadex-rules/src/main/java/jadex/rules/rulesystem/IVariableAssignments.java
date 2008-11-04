package jadex.rules.rulesystem;

/**
 *  Interface for an object that holds variable
 *  assignments.
 */
public interface IVariableAssignments
{
	/**
	 *  Get a variable assignment.
	 *  @param var The variable name.
	 *  @return The variable assignment.
	 */
	public Object	getVariableValue(String var);
	
	/**
	 *  Get the variable names.
	 *  @return All variable names.
	 */
	public String[] getVariableNames();
}
