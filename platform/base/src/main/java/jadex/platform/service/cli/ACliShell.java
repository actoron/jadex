package jadex.platform.service.cli;

import java.util.Map;

import jadex.commons.Tuple2;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Abstract base class for shells.
 */
public abstract class ACliShell
{
	//-------- attributes --------
	
	/** The session id. */
	protected Tuple2<String, Integer> sessionid;
	
	/** The current working dir. */
	protected String workingdir;
	
	//-------- constructors --------
	
	/**
	 *  Create a new cli.
	 */
	public ACliShell(Tuple2<String, Integer> sessionid)
	{
		this.sessionid = sessionid;
		this.workingdir = ".";
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
	 *  Get the session id.
	 *  @return The session id.
	 */
	public Tuple2<String, Integer> getSessionId()
	{
		return sessionid;
	}

	/**
	 *  Get the workingdir.
	 *  @return The workingdir.
	 */
	public String getWorkingDir()
	{
		return workingdir;
	}

	/**
	 *  Set the workingdir.
	 *  @param workingdir The workingdir to set.
	 */
	public void setWorkingDir(String workingdir)
	{
		this.workingdir = workingdir;
	}
}
