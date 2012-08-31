/**
 * 
 */
package jadex.platform.service.cli;

import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.util.Map;

/**
 *
 */
public abstract class ACliShell
{

	//-------- attributes --------
	
	/** The session id. */
	protected String sessionid;
	
	//-------- constructors --------
	
	/**
	 *  Create a new cli.
	 */
	public ACliShell(String session)
	{
		this.sessionid = session;
	}
	
	//-------- methods --------
	
	/**
	 *  Add a command.
	 *  @param cmd The command.
	 */
	public abstract IFuture<Void> addCommand(ICliCommand cmd);
	
	/**
	 *  Add all commands from classpath.
	 *  @param cl The classloader to use.
	 */
	public abstract IFuture<Void> addAllCommandsFromClassPath();
	
	/**
	 *  Get the commands.
	 *  @return The commands.
	 */
	public abstract Map<String, ICliCommand> getCommands();

	/**
	 *  Execute a command line command and
	 *  get back the results.
	 *  @param command The command.
	 *  @return The result of the command.
	 */
	public abstract IFuture<String> executeCommand(String line);
	
	/**
	 *  Add a subshell.
	 */
	public abstract void addSubshell(ACliShell subshell);
	
	/**
	 *  Remove a subshell.
	 */
	public abstract IFuture<Boolean> removeSubshell();
	
	/**
	 *  Get the complete prompt.
	 *  Calls subshells getPrompt().
	 *  @return the complete prompt;
	 */
	public IFuture<String> getShellPrompt()
	{
		final Future<String> ret = new Future<String>();
		
		internalGetShellPrompt().addResultListener(new DelegationResultListener<String>(ret)
		{
			public void customResultAvailable(String result)
			{
				ret.setResult(result+">");
			}
		});
		
		return ret;
	}
	
	/**
	 *  Get the complete prompt (internal method).
	 *  Calls subshells getPrompt().
	 *  @return the complete prompt;
	 */
	public abstract IFuture<String> internalGetShellPrompt();
	
	/**
	 * 
	 */
	public String getSessionId()
	{
		return sessionid;
	}
}
