package jadex.platform.service.cli;

import java.util.Map;

import jadex.commons.future.IFuture;

/**
 *  The command line command interface.
 */
public interface ICliCommand
{
	/**
	 *  Get the command names (name including alias').
	 *  @return A string array of the command name and optional further alias names.
	 */
	public String[] getNames();
	
	/**
	 *  Get the command description.
	 *  @return The command description.
	 */
	public String getDescription();
	
	/**
	 *  Get example usage(s).
	 *  @return Example usages.
	 */
	public String getExampleUsage();
	
	/**
	 *  Get the argument info.
	 *  @param context The context.
	 *  @return The argument infos.
	 */
	public ArgumentInfo[] getArgumentInfos(CliContext context);

	/**
	 *  Get the result info.
	 *  @param context The context.
	 *  @return The result info.
	 */
	public ResultInfo getResultInfo(CliContext context, Map<String, Object> args);
	
	/**
	 *  Invoke the command.
	 *  @param context The context.
	 *  @param args The arguments.
	 */
	public Object invokeCommand(CliContext context, Map<String, Object> args);
	
	// used internally to execute the command, remove from interface?
	/**
	 *  Invoke the command.
	 *  @param context The context.
	 *  @param args The arguments.
	 */
	public IFuture<String> invokeCommand(CliContext context, String[] args);
}
