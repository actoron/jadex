/**
 * 
 */
package jadex.platform.service.cli;

import java.util.Map;

import jadex.commons.Tuple2;
import jadex.commons.future.IFuture;

/**
 *  A remote shell is backed by an internal client service
 *  to which the commands are forwarded.
 */
public class RemoteCliShell extends ACliShell
{
	/** The client service used to delegate commands to. */
	protected IInternalCliService cliser;
	
	/**
	 *  Create a new remote shell.
	 */
	public RemoteCliShell(IInternalCliService cliser, Tuple2<String, Integer> sessionid)
	{
		super(sessionid);
		this.cliser = cliser;
	}
	
	/**
	 * 
	 */
	public IFuture<String> executeCommand(String line)
	{
		return cliser.executeCommand(line, sessionid);
	}
	
	//-------- methods --------
	
	/**
	 *  Get the complete prompt (internal method).
	 *  Calls subshells getPrompt().
	 *  @return the complete prompt;
	 */
	public IFuture<String> internalGetShellPrompt()
	{
		return cliser.internalGetShellPrompt(sessionid);
	}
	
	/**
	 *  Add all commands from classpath.
	 *  @param cl The classloader to use.
	 */
	public IFuture<Void> addAllCommandsFromClassPath()
	{
		return cliser.addAllCommandsFromClassPath(sessionid);
	}
	
	/**
	 *  Add a command.
	 *  @param cmd The command.
	 */
	public IFuture<Void> addCommand(ICliCommand cmd)
	{
		return cliser.addCommand(cmd, sessionid);
	}
	
	/**
	 *  Remove a subshell.
	 */
	public IFuture<Boolean> removeSubshell()
	{
		return cliser.removeSubshell(sessionid);
	}
	
	/**
	 *  Get the commands.
	 *  @return The commands.
	 */
	public Map<String, ICliCommand> getCommands()
	{
		// todo:
		return null;
	}
	
	/**
	 *  Add a subshell.
	 */
	public void addSubshell(ACliShell subshell)
	{
		throw new UnsupportedOperationException();
	}
}
