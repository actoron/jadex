package jadex.bridge;

/**
 *  Interface for a component step.
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
