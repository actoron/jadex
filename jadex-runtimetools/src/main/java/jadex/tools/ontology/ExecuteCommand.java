package jadex.tools.ontology;


/**
 *  Java class for concept ExecuteCommand of jadex.tools.introspector ontology.
 */
public class ExecuteCommand extends ToolAction
{
	//-------- attributes ----------

	/** The command to execute. */
	protected String	command;

	//-------- constructors --------

	/**
	 *  Default Constructor.
	 *  Create a new ExecuteCommand.
	 */
	public ExecuteCommand()
	{
	}

	/**
	 *  Init Constructor. <br>
	 *  Create a new ExecuteCommand.<br>
	 *  Initializes the object with required attributes.
	 * @param command
	 * @param tooltype
	 */
	public ExecuteCommand(String command, String tooltype)
	{
		this();
		setCommand(command);
		setToolType(tooltype);
	}

	//-------- accessor methods --------

	/**
	 *  Get the command of this ExecuteCommand.
	 *  The command to execute.
	 * @return command
	 */
	public String getCommand()
	{
		return this.command;
	}

	/**
	 *  Set the command of this ExecuteCommand.
	 *  The command to execute.
	 * @param command the value to be set
	 */
	public void setCommand(String command)
	{
		this.command = command;
	}

	//-------- additional methods --------

	/**
	 *  Get a string representation of this ExecuteCommand.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "ExecuteCommand(" + "command=" + getCommand() + ", tooltype=" + getToolType() + ")";
	}

}
