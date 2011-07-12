package jadex.bridge;

/**
 *  Interface for a component step.
 *  
 *  For making steps in anonymous inner classes properly serializable
 *  a static field for the name has to be declared:<br>
 *  public static final String XML_CLASSNAME = ...; 
 */
public interface IComponentStep
{
	/**
	 *  Execute the command.
	 *  @param args The argument(s) for the call.
	 *  @return The result of the command.
	 */
	public Object execute(IInternalAccess ia);
}
